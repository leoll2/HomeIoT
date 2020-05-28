#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "contiki.h"
#include "coap-engine.h"
#include "net/linkaddr.h"
#include "sys/etimer.h"
#include "sys/log.h"

#define LOG_MODULE "THERMO"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t res_thermo;
extern struct process registration_client;
extern unsigned long current_temp;
extern void update_temperature();
extern int temperature_zone_changed();

static struct etimer short_timer;
static struct etimer long_timer;

PROCESS(coap_server_thermo, "Thermometer Coap server");
AUTOSTART_PROCESSES(&coap_server_thermo);

const char res_path[] = "thermo";


PROCESS_THREAD(coap_server_thermo, ev, data)
{
    PROCESS_BEGIN();

    srand(time(NULL));

    LOG_INFO("Initializing thermometer [id=%d]\n", linkaddr_node_addr.u8[1]);
    coap_activate_resource(&res_thermo, "thermo");

    etimer_set(&short_timer, 10*CLOCK_SECOND);
    etimer_set(&long_timer, 60*CLOCK_SECOND);

    process_start(&registration_client, NULL);

    while(1) {
        PROCESS_WAIT_EVENT();

        // Simulate changes of temperature
        update_temperature();

        if (etimer_expired(&long_timer)) {
            res_thermo.trigger();
            etimer_reset(&long_timer);
            etimer_reset(&short_timer);
        } else if (etimer_expired(&short_timer)) {
            if (temperature_zone_changed() > 0)
                res_thermo.trigger();
            etimer_reset(&short_timer);
        }
    }

    PROCESS_END();
}