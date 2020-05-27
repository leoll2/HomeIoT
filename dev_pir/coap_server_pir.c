#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"
#include "os/dev/button-hal.h"
#include "sys/log.h"

#define LOG_MODULE "PIR"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t  res_pir;
extern struct process registration_client;

PROCESS(coap_server_pir, "PIR Coap server");
AUTOSTART_PROCESSES(&coap_server_pir);

const char res_path[] = "pir";


PROCESS_THREAD(coap_server_pir, ev, data)
{
    button_hal_button_t *btn;   /* Emulate the PIR with a button */
    btn = button_hal_get_by_index(0);

    PROCESS_BEGIN();

    LOG_INFO("Initializing PIR\n");
    coap_activate_resource(&res_pir, "pir");

    process_start(&registration_client, NULL);

    while(1) {
        PROCESS_WAIT_EVENT();
        if (ev == button_hal_press_event && data == btn) {
                LOG_DBG("PIR triggered\n");
                res_pir.trigger();
        }
    }

    PROCESS_END();
}