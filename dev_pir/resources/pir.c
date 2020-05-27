#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "PIR"
#define LOG_LEVEL LOG_LEVEL_APP

static int counter = 0;

// Forward declarations
static void res_event_handler(void);
static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);

EVENT_RESOURCE(res_pir,
               "{title:\"PIR sensor\", rt:\"pir\", ops:\"GET|POST|PUT\"}",
               res_get_handler,
               NULL,
               NULL,
               NULL, 
               res_event_handler);


static void res_event_handler(void)
{
    LOG_DBG("about to notify observers\n");
    counter++;
    // Notify all the observers
    coap_notify_observers(&res_pir);
}


static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset)
{
    coap_set_header_content_format(response, TEXT_PLAIN);
    coap_set_payload(response, buffer, snprintf((char *)buffer, 
                     preferred_size, "EVENT %lu", 
                     (unsigned long) counter));
}
