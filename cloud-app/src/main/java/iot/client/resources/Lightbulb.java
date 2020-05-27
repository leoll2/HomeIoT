package iot.client.resources;

import iot.client.ConstrainedDeviceResource;

public class Lightbulb extends ConstrainedDeviceResource {
	
	private Boolean on;
	
	 public static boolean matchesCommandSignature(String rt) {
        return rt.equals("bulb");
    }
	
	public Lightbulb(String path, String title, String rt, String ops) {
		super(path, title, rt, ops);
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
}
