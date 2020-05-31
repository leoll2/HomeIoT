package iot.client.resources;

import iot.client.ConstrainedDeviceResource;

public class AirConditioner extends ConstrainedDeviceResource {

	private Boolean on;

	public static boolean matchesCommandSignature(String rt) {
		return rt.equals("aircond");
	}

	public AirConditioner(String path, String title, String rt, String id, String ip) {
		super(path, title, rt, id, ip);
		on = false;
	}

	public Boolean isOn() {
		return on;
	}

	public void switchOn() {
		on = true;
	}

	public void switchOff() {
		on = false;
	}

	public void toggle() {
		on = !on;
	}

	public void set(Boolean val) {
		on = val;
	}

	@Override
	public void update(String observe_message) {
		if (observe_message.equals("on")) {
			switchOn();
		} else if (observe_message.equals("off")) {
			switchOff();
		} else {
			System.err.println("Observe notification for heater contains unrecognizable content: " + observe_message);
		}
	}

	@Override
	public String doRead() {
		return String.format("Status: %s\n", (this.on ? "ON" : "OFF"));
	}

	@Override
	public String doSet() {

		if (this.coap_client.doPost("mode=toggle"))
			return "Done";
		else
			return "Failed";
	}

	@Override
	public String doSet(String val) {

		if (!(val.equals("on") || val.equals("off")))
			return ("Invalid value: " + val);

		if (this.coap_client.doPost("mode=" + val))
			return "Done";
		else
			return "Failed";
	}
}