package iot.client.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import iot.client.ConstrainedDeviceResource;

public class Thermometer extends ConstrainedDeviceResource {

	private int current_temp;
	private int update_counter;
	private Date last_update;

	public static boolean matchesCommandSignature(String rt) {

		return rt.equals("thermo");
	}

	public Thermometer(String path, String title, String rt, String ops, String ip) {

		super(path, title, rt, ops, ip);
		current_temp = 240;
		update_counter = 0;
		last_update = new Date();
	}

	public int getCurrentTemperature() {

		return current_temp;
	}

	@Override
	public void update(String observe_message) {

		int cnt;
		int temp;
		JSONObject msgJSON;

		try {
			msgJSON = new JSONObject(observe_message);
			cnt = msgJSON.getInt("cnt");
			temp = msgJSON.getInt("temp");
		} catch (JSONException je) {
			System.err.println("Invalid temperature update (bad JSON)" + observe_message);
			return;
		}

		if (cnt > update_counter) {
			update_counter = cnt;
			current_temp = temp;
			last_update = new Date();
		}
	}

	@Override
	public String doRead() {

		final String date_pattern = "MM/dd/yyyy HH:mm:ss";
		final DateFormat df = new SimpleDateFormat(date_pattern);
		return String.format("Temperature: %.1f°C  \tLast update: %s\n", (current_temp / 10.0),
				df.format(this.last_update));
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