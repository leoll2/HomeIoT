#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"
#include "net/ipv6/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ipv6/uip-debug.h"
#include "os/dev/button-hal.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "PIR"
#define LOG_LEVEL LOG_LEVEL_APP

#define IP_INTERVAL     (2*CLOCK_SECOND)

extern coap_resource_t  res_pir;

static void print_addresses()
{
    int i;
    uint8_t state;
    printf("IPv6 addresses: ");
    for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
        state = uip_ds6_if.addr_list[i].state;
        if(uip_ds6_if.addr_list[i].isused && (state == ADDR_TENTATIVE || state == ADDR_PREFERRED)) {
            uip_debug_ipaddr_print(&uip_ds6_if.addr_list[i].ipaddr);
            if (state == ADDR_TENTATIVE)    printf("tentative\n");
            else if (state == ADDR_PREFERRED) printf("preferred\n");

            if (uip_is_addr_linklocal(&uip_ds6_if.addr_list[i].ipaddr)) printf("linklocal\n");
            printf("\n");
        }
    }
}


PROCESS(coap_server_pir, "PIR Coap server");
AUTOSTART_PROCESSES(&coap_server_pir);

PROCESS_THREAD(coap_server_pir, ev, data)
{
    static struct etimer periodic_timer;
    button_hal_button_t *btn;   /* Emulate the PIR with a button */
    btn = button_hal_get_by_index(0);

    PROCESS_BEGIN();

    LOG_INFO("Initializing PIR\n");

    coap_activate_resource(&res_pir, "pir");

    // Print IP address periodically
    etimer_set(&periodic_timer, IP_INTERVAL);
    while(1) {
        PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&periodic_timer));
        etimer_reset(&periodic_timer);
        print_addresses();
    }

    while(1) {
        PROCESS_WAIT_EVENT();
        if (ev == button_hal_press_event && data == btn) {
                LOG_DBG("PIR triggered\n");
                res_pir.trigger();
        }
    }

    PROCESS_END();
}