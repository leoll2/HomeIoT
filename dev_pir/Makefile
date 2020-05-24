CONTIKI_PROJECT = coap-server
all: $(CONTIKI_PROJECT)
CONTIKI = ../../..

# The BR is either native or embedded, and in the latter case must support SLIP
PLATFORMS_EXCLUDE = nrf52dk z1
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
MODULES_REL += ./resources

MODULES += $(CONTIKI_NG_APP_LAYER_DIR)/coap

# Include optional target-specific module
include $(CONTIKI)/Makefile.identify-target
MODULES_REL += $(TARGET)

include $(CONTIKI)/Makefile.include
