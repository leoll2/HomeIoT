package iot.client;

import java.util.LinkedList;
import java.util.List;

public class ConstrainedDevice {
	
	private String ipv6;
	private List<ConstrainedDeviceResource> resources;
	
	ConstrainedDevice(String ipv6) {
		this.ipv6 = ipv6;
		this.resources = new LinkedList<ConstrainedDeviceResource>();
		System.out.println("Created ConstrainedDevice with ipv6 " + ipv6);
	}
	
	
	public String getIpv6Addr() {
		return ipv6;
	}
	
	
	public Boolean registerResource(String path, String desc, String rt, String ops) {
		
		Boolean exist = false;
		for (ConstrainedDeviceResource r : resources) {
			if (r.getPath().equals(path)) {
				exist = true;
				r.refresh();
			}
		}
		if (! exist) {
			resources.add(ConstrainedDeviceResource.getResource(path, desc, rt, ops));
		}
		return exist;
	}
	
	
	public void cleanExpiredServices() {
		// TODO fare locking dell'array delle risorse
		resources.removeIf(r -> r.isExpired());
	}
	
	
	public String toString() {
		String s = "[" + ipv6 + "]:\n";
		for (ConstrainedDeviceResource r : resources) {
			s = s.concat(r.toString());
		}
		return s;
	}
}
