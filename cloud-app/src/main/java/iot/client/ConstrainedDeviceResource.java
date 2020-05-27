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

public class ConstrainedDeviceResource {

	private String path; 	// location of the resource (unique per device)
	private String title;	// human-readable description
	private String rt; 		// resource type, semantic type of the resource
	private String ops; 	// description of allowed operations
	private Date exp_time;	// expiration time
	private static ArrayList<Class<? extends ConstrainedDeviceResource>> res_types;
	
	private static final long MILLIS_PER_MINUTE = 60000;
	private static final long EXPIRATION_MINUTES = 5;
	
	static {
    	res_types = new ArrayList<>();
    	res_types.add(Lightbulb.class);
    	res_types.add(Pir.class);
    }
	
	protected ConstrainedDeviceResource(String path, String title, String rt, String ops) {
		this.path = path;
		this.title = title;
		this.rt = rt;
		this.ops = ops;
		this.refresh();
	}
	
	public static ConstrainedDeviceResource getResource(String path, String title, String rt, String ops) {
		
		ConstrainedDeviceResource res = null;
		
		for (Class<? extends ConstrainedDeviceResource> r : res_types) {
			try {
                // Get the method to verify the signature of this specific Command
                Method m = r.getMethod("matchesCommandSignature", String.class);
                
                // Check if the input text matches that signature
                boolean signatureMatches = (Boolean)m.invoke(null, rt);
               
                // If it corresponds, get the constructor and create a new instance
                if (signatureMatches) {
                    Constructor ctr = r.getConstructor(String.class, String.class, String.class, String.class);
                    res = (ConstrainedDeviceResource)ctr.newInstance(path, title, rt, ops);
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
	
	public String getPath() {
		return path;
	}
	
	public String getTitle() {
		return title;
	}

	public String getRt() {
		return rt;
	}

	public String getOps() {
		return ops;
	}
	
	public void refresh() {
		Calendar date = Calendar.getInstance();
		long now = date.getTimeInMillis();
		this.exp_time = new Date(now + (EXPIRATION_MINUTES * MILLIS_PER_MINUTE));
	}
	
	public Boolean isExpired() {
		return exp_time.before(new Date());
	}

	public String toString() {
		return "  path: " + this.path + "\n" +
			   "  title: " + this.title + "\n" + 
			   "  rt: " + this.rt + "\n" + 
			   "  ops: " + this.ops + "\n";
	}
}
