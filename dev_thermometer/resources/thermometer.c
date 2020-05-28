#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "THERMO"
#define LOG_LEVEL LOG_LEVEL_APP

#define MIN_TEMP 120      // minumum simulated temperature
#define MAX_TEMP 300      // maximum simulated temperature
#define LOW_THRESH 180    // threshold to trigger 'too cold' condition
#define HIGH_THRESH 240   // threshold to trigger 'too hot' condition
#define FLUCTUATION 10    // how much the temperature can vary between consecutive readings

unsigned long previous_temp = 210;
unsigned long current_temp = 210;   // [10*Celsius]
static unsigned long act_counter = 0;

// Forward declarations
static void res_event_handler(void);
static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset);

EVENT_RESOURCE(res_thermo,
               "{title:\"Thermometer\", rt:\"thermo\", ops:\"GET\"}",
               res_get_handler,
               NULL,
               NULL,
               NULL, 
               res_event_handler);


void update_temperature()
{
    previous_temp = current_temp;

    current_temp += (rand() % (2*FLUCTUATION+1)) - FLUCTUATION;
    // Clamp
    if (current_temp < MIN_TEMP)
        current_temp = MIN_TEMP;
    else if (current_temp > MAX_TEMP)
        current_temp = MAX_TEMP;

    LOG_DBG("Measured temperature: %d\n", current_temp);
}

int temperature_zone_changed()
{
    // Getting cold...
    if (current_temp < LOW_THRESH && previous_temp > LOW_THRESH) {
        LOG_DBG("It's getting cold...\n");
        return 1;
    }
    // Getting hot...
    if (current_temp > HIGH_THRESH && previous_temp < HIGH_THRESH) {
        LOG_DBG("It's getting hot...\n");
        return 1;
    }
    // Back to normal (comfortable)
    if (current_temp > LOW_THRESH && previous_temp < LOW_THRESH) {
        LOG_DBG("Temperature is ok again\n");
        return 1;
    }
    if (current_temp < HIGH_THRESH && previous_temp > HIGH_THRESH) {
        LOG_DBG("Temperature is ok again\n");
        return 1;
    }
    return 0;
}

static void res_event_handler(void)
{
    act_counter++;
    LOG_DBG("Advertising temperature: %d\n", current_temp);
    coap_notify_observers(&res_thermo);
}


static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer, uint16_t preferred_size,
                            int32_t *offset)
{
    coap_set_header_content_format(response, APPLICATION_JSON);
    coap_set_payload(response, buffer, snprintf((char *)buffer, 
                     64, "{cnt:%lu,temp:%lu}", 
                     act_counter, current_temp));
}
