#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Bulb"
#define LOG_LEVEL LOG_LEVEL_APP

/* Declare and auto-start this file's process */
PROCESS(coap_server_bulb, "Lightbulb CoAP server");
AUTOSTART_PROCESSES(&coap_server_bulb);

// extern coap_resource_t res_leds;
extern coap_resource_t res_lightbulb;


PROCESS_THREAD(coap_server_bulb, ev, data)
{
	PROCESS_BEGIN();

	LOG_INFO("Initializing lightbulb\n");
	coap_activate_resource(&res_lightbulb, "lightbulb");

	while (1) {
		PROCESS_WAIT_EVENT();
	}

	PROCESS_END();
}
