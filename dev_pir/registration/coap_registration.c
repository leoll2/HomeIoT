#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "PIR"
#define LOG_LEVEL LOG_LEVEL_APP

#define SERVER_EP   "coap://[2001:db8:0:f101::1]:5683"
#define REGISTRATION_INTERVAL   30

const char *service_url = "/rd";

char res_desc[64];


PROCESS(registration_client, "Registration client");

static struct etimer et;


void registration_resp_handler(coap_message_t *response)
{
    const uint8_t *chunk;
    int len;

    if (response == NULL) {
        puts("Request timed out");
        return;
    }

    len = coap_get_payload(response, &chunk);
    printf("|%.*s", len, (char *)chunk);
}


int has_local_ip()
{
    int i;
    uint8_t state;

    for (i = 0; i < UIP_DS6_ADDR_NB; i++) {
        state = uip_ds6_if.addr_list[i].state;
        if (uip_ds6_if.addr_list[i].isused 
                && (state == ADDR_PREFERRED)
                && !uip_is_addr_linklocal(&uip_ds6_if.addr_list[i].ipaddr)) {
            return 1;
        }
    }
    return 0;
}


PROCESS_THREAD(registration_client, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    sprintf(res_desc, "{p:\"pir\",d:\"PIR sensor\",t:\"pir\",id:\"%d\"}", linkaddr_node_addr.u8[1]);

    PROCESS_BEGIN();

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    etimer_set(&et, REGISTRATION_INTERVAL * CLOCK_SECOND);

    while(1) {
        PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&et));

        if (has_local_ip()) {
            printf("Registering resources\n");
            
            coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
            coap_set_header_uri_path(request, service_url);

            coap_set_payload(request, (uint8_t *)res_desc, sizeof(res_desc) - 1);
            COAP_BLOCKING_REQUEST(&server_ep, request, registration_resp_handler);
        }
        etimer_reset(&et);
    }
    PROCESS_END();
}