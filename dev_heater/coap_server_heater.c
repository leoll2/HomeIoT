#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/log.h"
#include "net/linkaddr.h"

#define LOG_MODULE "Heater"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t res_heater;
extern struct process registration_client;

PROCESS(coap_server_heater, "Heater CoAP server");
AUTOSTART_PROCESSES(&coap_server_heater);

const char res_path[] = "heater";


PROCESS_THREAD(coap_server_heater, ev, data)
{
	PROCESS_BEGIN();

	LOG_INFO("Initializing heater [id=%d]\n", linkaddr_node_addr.u8[1]);

	coap_activate_resource(&res_heater, "heater");

	process_start(&registration_client, NULL);

	while (1) {
		PROCESS_YIELD();
	}

	PROCESS_END();
}
