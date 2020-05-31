#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

#include <string.h>


/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Heater"
#define LOG_LEVEL LOG_LEVEL_APP

#define HEATER_OFF    0
#define HEATER_ON     1

static int heater_status = HEATER_OFF;      // 0: off, 1: on
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


/* Heater, can be turned on and off */
EVENT_RESOURCE(res_heater,
               "{title:\"Heater\", rt:\"heater\", ops:\"GET|POST|PUT\"}",
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

    /* Current status of the heater */
    len = strlen(get_responses[heater_status]);
    memcpy(buffer, get_responses[heater_status], len);

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
    uint8_t led = LEDS_RED;     /* Heater emulated by red LED */
    int success = 0;

    len = coap_get_post_variable(request, "mode", &mode);

    if (len > 0) {
        LOG_DBG("mode %s\n", mode);

        if (strncmp(mode, "on", len) == 0) {
            leds_on(LEDS_NUM_TO_MASK(led));
            heater_status = HEATER_ON;
            success = 1;
        } else if (strncmp(mode, "off", len) == 0) {
            leds_off(LEDS_NUM_TO_MASK(led));
            heater_status = HEATER_OFF;
            success = 1;
        } else if (strncmp(mode, "toggle", len) == 0) {
            if (heater_status == HEATER_OFF) {
                leds_on(LEDS_NUM_TO_MASK(led));
                heater_status = HEATER_ON;
            } else {
                leds_off(LEDS_NUM_TO_MASK(led));
                heater_status = HEATER_OFF;
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
        res_heater.trigger();
    }
}


static void res_event_handler(void)
{
    coap_notify_observers(&res_heater);
}

