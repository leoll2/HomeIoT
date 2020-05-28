package iot.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

public class ResourceDirectory extends CoapServer implements Runnable {
	
	private static ResourceDirectory rd;	
	private RDResource rdr;

	private class RDResource extends CoapResource {
		
		private List<ConstrainedDevice> devices_registry;
		
		// Constructor
		RDResource(String name) {
			
			super(name);
			devices_registry = new LinkedList<ConstrainedDevice>();
		}
		
		
		private Boolean isValidRequest(byte[] request) {
			
			if (request.length == 0)
				return false;
			// TODO add regex checks
			return true;
		}
		
		
		private ConstrainedDevice findDevice(String ipv6) {
			for (ConstrainedDevice dev : devices_registry) {
				if (dev.getIpv6Addr().equals(ipv6)) {
					return dev;
				}
			}
			return null;
		}
		
		
		private void cleanExpiredServices() {
			for (ConstrainedDevice dev : devices_registry) {
				dev.cleanExpiredServices();
			}
		}
		
		
		private ConstrainedDevice createDevice(String ip) {
			ConstrainedDevice dev = new ConstrainedDevice(ip);
			devices_registry.add(dev);
			return dev;
		}
		
		
		/* Method to handle POST requests (registrations) */
		public void handlePOST(CoapExchange exchange) {
		
			Response response;
			JSONObject requestJSON;
			
			// Retrieve sender and payload
			InetAddress src_addr = exchange.getSourceAddress();
			byte[] requestBody = exchange.getRequestPayload();
			
			// Validate
			if (isValidRequest(requestBody)) {
				response = new Response(ResponseCode.BAD_REQUEST);			
				exchange.respond(response);
			}
			
			// Parse JSON
			try {
				requestJSON = new JSONObject(new String(requestBody));
			} catch (JSONException je) {
				System.err.println("Invalid registration request: " + new String(requestBody));
				response = new Response(ResponseCode.BAD_REQUEST);			
				exchange.respond(response);
				return;
			}
			if (! (requestJSON.has("p") && requestJSON.has("d") 
					&& requestJSON.has("t") && requestJSON.has("id"))) {
				System.err.println("Invalid registration request: " + requestJSON.toString());
			}
			
			// Get the device, or create if doesn't exist
			ConstrainedDevice dev = findDevice(src_addr.getHostAddress());
			if (dev == null) {
				dev = createDevice(src_addr.getHostAddress());
			}
			// Register the resource (or refresh it it already existed)
			Boolean existed = dev.registerResource(
					requestJSON.getString("p"),
					requestJSON.getString("d"),
					requestJSON.getString("t"),
					requestJSON.getString("id"));
			if (existed)
				response = new Response(ResponseCode.CHANGED);  // resource refreshed
			else
				response = new Response(ResponseCode.CREATED);	// new resource created
			
			exchange.respond(response);
	 	}
		
		
		public String toString() {
			String s = 
					"-----------------------------------------------------------------------------\n" +
					String.format("%-25s | %-16s | %-20s | %-8s \n", "IPv6", "Path", "Description", "NodeID") +
					"-----------------------------------------------------------------------------\n";
			for (ConstrainedDevice d : devices_registry) {
				s = s.concat(d.toString());
			}
			return s;
		}
	}

	
	/* Constructor */
	private ResourceDirectory() {
		
		rdr = new RDResource("rd");
	}
	
	
	public static ResourceDirectory getResourceDirectory() {
		
		if (rd == null) {
			rd = new ResourceDirectory();
		}
		return rd;
	}
	
	
	public ConstrainedDeviceResource findResourceByIpPath(String ipv6, String path) {
		
		ConstrainedDevice dev = rdr.findDevice(ipv6);
		if (dev == null)
			return null;
		return dev.findResourceByPath(path);
	}
	
	
	public List<ConstrainedDeviceResource> findResourcesByType(String type) {
		
		List<ConstrainedDeviceResource> ress = new ArrayList<>();
		for (ConstrainedDevice dev : rdr.devices_registry) {
			ress.addAll(dev.findResourcesByType(type));
		}
		return ress;
	}
	
	
	public String toString() {
		
		return rdr.toString();
	}
	
	
	public void run() {

		this.add(rdr);
		this.start();
		
		while (true) {
			try {
				rdr.cleanExpiredServices();
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}