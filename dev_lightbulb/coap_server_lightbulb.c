#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/log.h"

#define LOG_MODULE "Bulb"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t res_lightbulb;
extern struct process registration_client;

PROCESS(coap_server_bulb, "Lightbulb CoAP server");
AUTOSTART_PROCESSES(&coap_server_bulb);

const char res_path[] = "bulb";


PROCESS_THREAD(coap_server_bulb, ev, data)
{
	PROCESS_BEGIN();

	LOG_INFO("Initializing lightbulb\n");

	coap_activate_resource(&res_lightbulb, "bulb");

	process_start(&registration_client, NULL);

	while (1) {
		PROCESS_YIELD();
	}

	PROCESS_END();
}
