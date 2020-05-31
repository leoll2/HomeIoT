#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/log.h"
#include "net/linkaddr.h"

#define LOG_MODULE "AirConditioner"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t res_aircond;
extern struct process registration_client;

PROCESS(coap_server_aircond, "AirConditioner CoAP server");
AUTOSTART_PROCESSES(&coap_server_aircond);

const char res_path[] = "aircond";


PROCESS_THREAD(coap_server_aircond, ev, data)
{
	PROCESS_BEGIN();

	LOG_INFO("Initializing aircond [id=%d]\n", linkaddr_node_addr.u8[1]);

	coap_activate_resource(&res_aircond, "aircond");

	process_start(&registration_client, NULL);

	while (1) {
		PROCESS_YIELD();
	}

	PROCESS_END();
}
