package iot.client.resources;

import iot.client.ConstrainedDeviceResource;

public class Lightbulb extends ConstrainedDeviceResource {
	
	private Boolean on;
	
	 public static boolean matchesCommandSignature(String rt) {
        return rt.equals("bulb");
    }
	
	public Lightbulb(String path, String title, String rt, String ops, String ip) {
		super(path, title, rt, ops, ip);
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
		on = ! on;
	}
	
	
	public void set(Boolean val) {
		on = val;
	}
	
	public String toString() {
		return super.toString() +
			   "  status: " + (this.on ? "ON" : "OFF") + "\n";
	}

	@Override
	public void update(String observe_message) {
		if (observe_message.equals("on")) {
			switchOn();
		} else if (observe_message.equals("off")) {
			switchOff();
		} else {
			System.err.println("Observe notification for bulb contains unrecognizable content: " + observe_message);
		}
	}
}

