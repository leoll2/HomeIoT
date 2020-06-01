package iot.client.smartfeatures;

import java.util.List;

import iot.client.ConstrainedDeviceResource;
import iot.client.ResourceDirectory;
import iot.client.resources.Thermometer;

public class SmartTemperature implements Runnable {

	private enum TempZone {
		COLD, NORMAL, HOT
	};

	private static int COLD_THRESH = 200; // = 180; TODO restore original threshold
	private static int HOT_THRESH = 220;  // = 240; TODO restore original threshold
	private static Boolean enabled = true;

	private static SmartTemperature smart_temp;
	private static ResourceDirectory res_dir;

	private TempZone curr_tempzone;
	private TempZone prev_tempzone;

	private SmartTemperature() {
		curr_tempzone = TempZone.NORMAL;
		prev_tempzone = TempZone.NORMAL;
	}

	public static SmartTemperature getSmartTemperatureInstance(ResourceDirectory res_dir) {

		SmartTemperature.res_dir = res_dir;
		if (smart_temp == null) {
			smart_temp = new SmartTemperature();
		}
		return smart_temp;
	}

	private Integer getAverageTemp() {

		int tot = 0;
		List<ConstrainedDeviceResource> thermometers = res_dir.findResourcesByType("thermo");
		for (ConstrainedDeviceResource th : thermometers) {
			tot += ((Thermometer) th).getCurrentTemperature();
		}
		if (thermometers.size() > 0)
			return tot / thermometers.size();
		else
			return null;
	}

	public static Integer getAvgTemp() {

		if (smart_temp != null)
			return smart_temp.getAverageTemp();
		else
			return null;
	}

	private TempZone getAverageTempZone() {

		Integer avg_temp = getAverageTemp();

		// If no thermometer is available, assume a normal temperature
		if (avg_temp == null)
			return TempZone.NORMAL;

		if (avg_temp < COLD_THRESH) {
			return TempZone.COLD;
		} else if (avg_temp > HOT_THRESH) {
			return TempZone.HOT;
		} else {
			return TempZone.NORMAL;
		}
	}

	private void doWarm() {

		List<ConstrainedDeviceResource> heaters = res_dir.findResourcesByType("heater");
		for (ConstrainedDeviceResource ht : heaters) {
			ht.doSet("on");
		}
	}

	private void doCool() {

		List<ConstrainedDeviceResource> conditioners = res_dir.findResourcesByType("aircond");
		for (ConstrainedDeviceResource cond : conditioners) {
			cond.doSet("on");
		}
	}

	private void doNormal() {

		List<ConstrainedDeviceResource> heaters = res_dir.findResourcesByType("heater");
		for (ConstrainedDeviceResource ht : heaters) {
			ht.doSet("off");
		}
		List<ConstrainedDeviceResource> conditioners = res_dir.findResourcesByType("aircond");
		for (ConstrainedDeviceResource cond : conditioners) {
			cond.doSet("off");
		}
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

			prev_tempzone = curr_tempzone;
			curr_tempzone = getAverageTempZone();

			if (enabled) {
				switch (curr_tempzone) {
				case COLD:
					if (prev_tempzone != TempZone.COLD) {
						// System.out.println("Warming...");
						doWarm();
					}
					break;
				case HOT:
					if (prev_tempzone != TempZone.HOT) {
						// System.out.println("Cooling...");
						doCool();
					}
					break;
				default:
					if (prev_tempzone != TempZone.NORMAL) {
						// System.out.println("Temperature is normal again...");
						doNormal();
					}
					break;
				}
			}

			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// nothing
			}
		}
	}

}
