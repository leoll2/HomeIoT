package iot.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import iot.client.commands.LsCommand;
import iot.client.commands.ReadCommand;
import iot.client.resources.Lightbulb;
import iot.client.resources.Pir;
import iot.client.resources.Thermometer;

public abstract class ConstrainedDeviceResource {

	private String path; 	// location of the resource (unique per device)
	private String desc;	// human-readable description
	private String rt; 		// resource type, semantic type of the resource
	private String nodeid;	// node ID
	private String ip; 		// ip of the device exposing this resource
	private Date exp_time;	// expiration time
	protected ResourceCoapClient coap_client; // CoAP client to handle operations on the resource
	private Thread coap_client_thread; 		// thread for the CoAP client
	
	private static ArrayList<Class<? extends ConstrainedDeviceResource>> res_types;
	
	private static final long MILLIS_PER_MINUTE = 60000;
	private static final long EXPIRATION_MINUTES = 5;
	
	static {
    	res_types = new ArrayList<>();
    	res_types.add(Lightbulb.class);
    	res_types.add(Pir.class);
    	res_types.add(Thermometer.class);
    }
	
	protected ConstrainedDeviceResource(String path, String desc, String rt, String id, String ip) {
		this.path = path;
		this.desc = desc;
		this.rt = rt;
		this.nodeid = id;
		this.ip = ip;
		this.refresh();
		this.coap_client = new ResourceCoapClient(ip, path, this);
		this.coap_client_thread = new Thread(this.coap_client);
		this.coap_client_thread.start();
	}
	
	public static ConstrainedDeviceResource getResource(String path, String desc, String rt, String id, String ip) {
		
		ConstrainedDeviceResource res = null;
		
		for (Class<? extends ConstrainedDeviceResource> r : res_types) {
			try {
                // Get the method to verify the signature of this specific Command
                Method m = r.getMethod("matchesCommandSignature", String.class);
                
                // Check if the input text matches that signature
                boolean signatureMatches = (Boolean)m.invoke(null, rt);
               
                // If it corresponds, get the constructor and create a new instance
                if (signatureMatches) {
                    Constructor ctr = r.getConstructor(String.class, String.class, String.class, String.class, String.class);
                    res = (ConstrainedDeviceResource)ctr.newInstance(path, desc, rt, id, ip);
                    break;
                }
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
                System.err.println("You probably forgot to implement method "
                                  + "'matchesCommandSignature' in the class " 
                                  + r.getName());
            } catch (IllegalAccessException   | InvocationTargetException |
                    IllegalArgumentException | InstantiationException e) {
               e.printStackTrace();
           }
		}
		
		return res;
	}
	
	public final String getIp() {
		return ip;
	}
	
	public final String getPath() {
		return path;
	}
	
	public final String getDesc() {
		return desc;
	}

	public final String getRt() {
		return rt;
	}

	public final String getNodeId() {
		return nodeid;
	}
	
	public final String getFullPath() {
		return String.format("[%s]:5683/%s", ip, path);
	}
	
	public abstract void update(String update_message);
	
	public abstract String doRead();
	
	public abstract String doSet();
	
	public abstract String doSet(String val);
	
	public void refresh() {
		Calendar date = Calendar.getInstance();
		long now = date.getTimeInMillis();
		this.exp_time = new Date(now + (EXPIRATION_MINUTES * MILLIS_PER_MINUTE));
	}
	
	public Boolean isExpired() {
		return exp_time.before(new Date());
	}

	public final String toString() {
		return String.format("%-25s | %-16s | %-20s | %-8s \n",
				"[" + this.ip + "]",
				this.path,
				this.desc,
				this.nodeid);
	}
}
