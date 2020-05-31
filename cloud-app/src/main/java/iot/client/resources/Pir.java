package iot.client.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import iot.client.ConstrainedDeviceResource;
import iot.client.smartfeatures.SmartLights;

public class Pir extends ConstrainedDeviceResource {

	private int activation_counter;
	private Date last_activation;

	public static boolean matchesCommandSignature(String rt) {

		return rt.equals("pir");
	}

	public Pir(String path, String title, String rt, String ops, String ip) {

		super(path, title, rt, ops, ip);
		activation_counter = 0;
		last_activation = new Date();
	}

	public Date getLastActivation() {
		return last_activation;
	}

	@Override
	public void update(String observe_message) {

		int msg_act_cnt;
		try {
			msg_act_cnt = Integer.parseInt(observe_message);
		} catch (NumberFormatException e) {
			System.err.println("Observe notification for PIR does not contain an integer: " + observe_message);
			return;
		}
		if (msg_act_cnt > activation_counter) {
			activation_counter = msg_act_cnt;
			last_activation = new Date();
			SmartLights.signalMotion();
		}
	}

	@Override
	public String doRead() {

		final String date_pattern = "MM/dd/yyyy HH:mm:ss";
		final DateFormat df = new SimpleDateFormat(date_pattern);
		return String.format("Last activation: %s\n", df.format(this.last_activation));
	}

	@Override
	public String doSet() {

		return "Operation not supported\n";
	}

	@Override
	public String doSet(String val) {

		return doSet();
	}
}
