package iot.client.resources;

import iot.client.ConstrainedDeviceResource;

public class Pir extends ConstrainedDeviceResource {
		
	 public static boolean matchesCommandSignature(String rt) {
        return rt.equals("pir");
    }
	
	public Pir(String path, String title, String rt, String ops) {
		super(path, title, rt, ops);
	}
	
	public String toString() {
		// TODO
		return super.toString();
	}
}