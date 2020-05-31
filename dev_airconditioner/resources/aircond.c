#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

#include <string.h>


/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "AirConditioner"
#define LOG_LEVEL LOG_LEVEL_APP

#define AIRCOND_OFF    0
#define AIRCOND_ON     1

static int aircond_status = AIRCOND_OFF;      // 0: off, 1: on
static const char *get_responses[2] = { // responses for GET requests
    "off",
    "on"
};


// Forward declarations
static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size, 
                            int32_t *offset);
static void res_post_handler(coap_message_t *request, coap_message_t *response,
                             uint8_t *buffer, uint16_t preferred_size,
                             int32_t *offset);
static void res_event_handler(void);


/* AirConditioner, can be turned on and off */
EVENT_RESOURCE(res_aircond,
               "{title:\"AirConditioner\", rt:\"aircond\", ops:\"GET|POST|PUT\"}",
               res_get_handler,
               res_post_handler,
               res_post_handler,
               NULL, 
               res_event_handler);


/* Method to handle GET requests */
static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size, 
                            int32_t *offset)
{
    int len;

    /* Current status of the aircond */
    len = strlen(get_responses[aircond_status]);
    memcpy(buffer, get_responses[aircond_status], len);

    coap_set_header_content_format(response, TEXT_PLAIN);
    coap_set_header_etag(response, (uint8_t *)&len, 1);
    coap_set_payload(response, buffer, len);
}


/* Method to handle POST/PUT requests */
static void res_post_handler(coap_message_t *request, coap_message_t *response,
                             uint8_t *buffer, uint16_t preferred_size,
                             int32_t *offset)
{
    size_t len = 0;
    const char *mode = NULL;
    uint8_t led = LEDS_RED;     /* AirConditioner emulated by red LED */
    int success = 0;

    len = coap_get_post_variable(request, "mode", &mode);

    if (len > 0) {
        LOG_DBG("mode %s\n", mode);

        if (strncmp(mode, "on", len) == 0) {
            leds_on(LEDS_NUM_TO_MASK(led));
            aircond_status = AIRCOND_ON;
            success = 1;
        } else if (strncmp(mode, "off", len) == 0) {
            leds_off(LEDS_NUM_TO_MASK(led));
            aircond_status = AIRCOND_OFF;
            success = 1;
        } else if (strncmp(mode, "toggle", len) == 0) {
            if (aircond_status == AIRCOND_OFF) {
                leds_on(LEDS_NUM_TO_MASK(led));
                aircond_status = AIRCOND_ON;
            } else {
                leds_off(LEDS_NUM_TO_MASK(led));
                aircond_status = AIRCOND_OFF;
            }
            success = 1;
        }
    } else {
        LOG_DBG("Received invalid POST/PUT request:");
        LOG_DBG(" - uri_host: %.*s\n", (int)request->uri_host_len, (char*)request->uri_host);
        LOG_DBG(" - location_path: %.*s\n", (int)request->location_path_len, (char*)request->location_path);
        LOG_DBG(" - uri_path: %.*s\n", (int)request->uri_path_len, (char*)request->uri_path);
        LOG_DBG(" - uri_query: %.*s\n", (int)request->uri_query_len, (char*)request->uri_query);
        LOG_DBG(" - payload: %.*s\n", (int)request->payload_len, (char*)request->payload);
    }

    if (!success) {
        coap_set_status_code(response, BAD_REQUEST_4_00);
    } else {
        // notify about the change
        res_aircond.trigger();
    }
}


static void res_event_handler(void)
{
    coap_notify_observers(&res_aircond);
}

