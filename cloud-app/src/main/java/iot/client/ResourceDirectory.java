package iot.client;

import java.net.InetAddress;
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
	
	private RDResource rdr;

	private class RDResource extends CoapResource {
		
		private List<ConstrainedDevice> devices_registry;
		
		// Constructor
		RDResource(String name) {
			super(name);
			devices_registry = new LinkedList<ConstrainedDevice>();
			System.out.println("Resource Directory created");
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
		
		
		/* Method to handle POST requests (registrations) */
		public void handlePOST(CoapExchange exchange) {
			
			Response response;
			JSONObject requestJSON;
			
			// Retrieve sender and payload
			InetAddress src_addr = exchange.getSourceAddress();
			System.out.println("Received POST request from: " + src_addr.getHostAddress());
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
				System.out.println("Invalid registration request: " + new String(requestBody));
				response = new Response(ResponseCode.BAD_REQUEST);			
				exchange.respond(response);
				return;
			}
			if (! (requestJSON.has("p") && requestJSON.has("d") 
					&& requestJSON.has("t") && requestJSON.has("o"))) {
				System.out.println("Invalid registration request: " + requestJSON.toString());
			}
			
			// 
			ConstrainedDevice dev = findDevice(src_addr.getHostAddress());
			if (dev == null) {
				// Device not already known, create it
				dev = new ConstrainedDevice(src_addr.getHostAddress());
				devices_registry.add(dev);
			}
			// Register the resource (or refresh it it already existed)
			Boolean existed = dev.registerResource(
					requestJSON.getString("p"),
					requestJSON.getString("d"),
					requestJSON.getString("t"),
					requestJSON.getString("o"));
			if (existed)
				response = new Response(ResponseCode.CHANGED);  // resource refreshed
			else
				response = new Response(ResponseCode.CREATED);	// new resource created
			
			exchange.respond(response);
	 	}
		
		public String toString() {
			String s = "rd:\n";
			for (ConstrainedDevice d : devices_registry) {
				s = s.concat(d.toString() + '\n');
			}
			return s;
		}
	}

	
	/* Constructor */
	ResourceDirectory() {
		rdr = new RDResource("rd");
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
