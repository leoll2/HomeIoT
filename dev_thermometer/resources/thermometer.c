#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "THERMO"
#define LOG_LEVEL LOG_LEVEL_APP

#define MIN_TEMP            120      // minumum simulated temperature
#define MAX_TEMP            300      // maximum simulated temperature
#define NOTIF_TEMP_DELTA    10       // max variation before triggering a notification
#define FLUCTUATION         5        // how much the temperature can vary between consecutive readings

unsigned long last_notif_temp = 210;
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
    current_temp += (rand() % (2*FLUCTUATION+1)) - FLUCTUATION;
    // Clamp
    if (current_temp < MIN_TEMP)
        current_temp = MIN_TEMP;
    else if (current_temp > MAX_TEMP)
        current_temp = MAX_TEMP;

    LOG_DBG("Measured temperature: %.1f\n", current_temp/10.0);
}

int large_temp_change()
{
    if (((current_temp > last_notif_temp) && (current_temp - last_notif_temp > NOTIF_TEMP_DELTA)) ||
        ((last_notif_temp > current_temp) && (last_notif_temp - current_temp > NOTIF_TEMP_DELTA))) {
        
        return 1;
    }
    return 0;
}

static void res_event_handler(void)
{
    act_counter++;
    LOG_DBG("Advertising temperature: %.1f\n", current_temp/10.0);
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
