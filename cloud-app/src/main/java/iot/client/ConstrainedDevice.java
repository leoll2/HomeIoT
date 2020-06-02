package iot.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedDevice {

	private String ipv6;
	private List<ConstrainedDeviceResource> resources;

	ConstrainedDevice(String ipv6) {

		this.ipv6 = ipv6;
		this.resources = new LinkedList<ConstrainedDeviceResource>();
	}

	/**
	 * Get the IPv6 of the node
	 * 
	 * @return String representing the IP address
	 */
	public String getIpv6Addr() {
		return ipv6;
	}

	/**
	 * Register a resource for this device
	 * 
	 * @param path Location of the resource
	 * @param desc Brief description of the resource
	 * @param rt   Resource type
	 * @param id   Node ID
	 * @return true if the resource already existed, false if it was created instead
	 */
	public Boolean registerResource(String path, String desc, String rt, String id) {

		Boolean exist = false;
		for (ConstrainedDeviceResource r : resources) {
			if (r.getPath().equals(path)) {
				exist = true;
				r.refresh();
			}
		}
		if (!exist) {
			ConstrainedDeviceResource res = ConstrainedDeviceResource.getResource(path, desc, rt, id, this.ipv6);
			if (res != null)
				resources.add(ConstrainedDeviceResource.getResource(path, desc, rt, id, this.ipv6));
			else
				System.err.println("Failed to register resource of type :" + rt);
		}
		return exist;
	}

	/**
	 * Remove resources that were registered long time ago and not refreshed since
	 * then
	 */
	public void cleanExpiredResources() {

		List<ConstrainedDeviceResource> toremove = new ArrayList<ConstrainedDeviceResource>();
		for (ConstrainedDeviceResource r : resources) {
			if (r.isExpired()) {
				toremove.add(r);
			}
		}
		for (ConstrainedDeviceResource r : toremove) {
			r.teardown();
		}
		resources.removeAll(toremove);
	}

	/**
	 * Get the resource matching the given path
	 * 
	 * @param path Path to identify the resource for this device
	 * @return the resource if found, null otherwise
	 */
	public ConstrainedDeviceResource findResourceByPath(String path) {

		for (ConstrainedDeviceResource res : resources) {
			if (res.getPath().equals(path)) {
				return res;
			}
		}
		return null;
	}

	/**
	 * Get all the resources of a given type for this device
	 * 
	 * @param type Type of resources to find
	 * @return List of resources found
	 */
	public List<ConstrainedDeviceResource> findResourcesByType(String type) {

		List<ConstrainedDeviceResource> ress = new ArrayList<>();
		for (ConstrainedDeviceResource res : resources) {
			if (res.getRt().equals(type)) {
				ress.add(res);
			}
		}
		return ress;
	}

	public String toString() {
		String s = "";
		for (ConstrainedDeviceResource r : resources) {
			s = s.concat(r.toString());
		}
		return s;
	}
}
