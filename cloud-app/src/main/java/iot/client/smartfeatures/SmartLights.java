package iot.client.smartfeatures;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import iot.client.ConstrainedDeviceResource;
import iot.client.ResourceDirectory;
import iot.client.resources.Pir;

public class SmartLights implements Runnable {

	private static SmartLights smartlights;
	private static Lock l;
	private static Condition motionDetect;
	private static ResourceDirectory res_dir;
	private static Boolean enabled = true;

	private SmartLights() {

		l = new ReentrantLock();
		motionDetect = l.newCondition();
	}

	public static SmartLights getSmartLightsInstance(ResourceDirectory res_dir) {

		SmartLights.res_dir = res_dir;
		if (smartlights == null) {
			smartlights = new SmartLights();
		}
		return smartlights;
	}

	public static void signalMotion() {

		if (enabled) {
			l.lock();
			motionDetect.signal();
			l.unlock();
		}
	}

	private Boolean matchingIds(int id1, int id2) {
		// Policy: two IDs match if they are equal modulo 3
		if (id1 % 3 == id2 % 3)
			return true;
		else
			return false;
	}

	public static void enable() {
		enabled = true;
	}

	public static void disable() {
		enabled = false;
	}

	public static Boolean isEnabled() {
		return enabled;
	}

	@Override
	public void run() {

		while (true) {
			l.lock();
			try {
				motionDetect.await();
				Calendar cal = Calendar.getInstance();
				long millis_now = cal.getTimeInMillis();
				Date few_seconds_ago = new Date(millis_now - (5000));
				List<ConstrainedDeviceResource> pirs = res_dir.findResourcesByType("pir");
				List<ConstrainedDeviceResource> lights = res_dir.findResourcesByType("bulb");
				for (ConstrainedDeviceResource pir : pirs) {
					Date last_act = ((Pir) pir).getLastActivation();
					// If that PIR was activated less than few seconds ago, turn on the
					// corresponding lights
					if (last_act.after(few_seconds_ago)) {
						int pir_id = Integer.parseInt(pir.getNodeId());
						for (ConstrainedDeviceResource light : lights) {
							int light_id = Integer.parseInt(light.getNodeId());
							if (matchingIds(pir_id, light_id)) {
								light.doSet("on");
							}
						}
					}
				}
			} catch (InterruptedException e) {
				// nothing
			} finally {
				l.unlock();
			}
		}
	}
}
