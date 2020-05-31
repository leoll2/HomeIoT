package iot.client.cli.commands;

import java.util.regex.Matcher;

import iot.client.ResourceDirectory;
import iot.client.cli.Command;
import iot.client.regex.RegexBox;
import iot.client.smartfeatures.SmartLights;
import iot.client.smartfeatures.SmartTemperature;

public class SmarttempCommand extends Command {

	private String mode;

	public SmarttempCommand(String text) {
		super(text);
		if (!matchesCommandSignature(text))
			throw new IllegalArgumentException();
		parseArgs();
	}

	public static boolean matchesCommandSignature(String text) {
		final Matcher m = RegexBox.SMARTTEMP_PATTERN.matcher(text);
		return m.matches();
	}

	@Override
	protected final boolean parseArgs() {

		final Matcher cmdMatcher = RegexBox.SMARTTEMP_PATTERN.matcher(this.commandText);
		cmdMatcher.find();
		this.mode = cmdMatcher.group(1);
		return true;
	}

	@Override
	public final String execute(ResourceDirectory res_dir) {

		if (mode.equals("on")) {
			SmartTemperature.enable();
			return "Smart temperature management enabled";
		} else if (mode.equals("off")) {
			SmartTemperature.disable();
			return "Smart temperature management disabled";
		} else if (mode.equals("status")) {
			return String.format("Smart temperature: %s  (avg = %.1f)",
					(SmartTemperature.isEnabled() ? "enabled" : "disabled"), SmartTemperature.getAvgTemp() / 10.0);
		} else {
			return "Smart temperature: unknown setting";
		}
	}

	public static String help() {

		return "smarttemp - Enable/disable smart temperature management";
	}

	public static String getKeyword() {

		return "smarttemp";
	}
}