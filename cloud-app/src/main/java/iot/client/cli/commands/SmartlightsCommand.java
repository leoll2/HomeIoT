package iot.client.cli.commands;

import java.util.regex.Matcher;

import iot.client.ResourceDirectory;
import iot.client.cli.Command;
import iot.client.regex.RegexBox;
import iot.client.smartfeatures.SmartLights;
import iot.client.smartfeatures.SmartTemperature;

public class SmartlightsCommand extends Command {

	private String mode;

	public SmartlightsCommand(String text) {
		super(text);
		if (!matchesCommandSignature(text))
			throw new IllegalArgumentException();
		parseArgs();
	}

	public static boolean matchesCommandSignature(String text) {
		final Matcher m = RegexBox.SMARTLIGHTS_PATTERN.matcher(text);
		return m.matches();
	}

	@Override
	protected final boolean parseArgs() {

		final Matcher cmdMatcher = RegexBox.SMARTLIGHTS_PATTERN.matcher(this.commandText);
		cmdMatcher.find();
		this.mode = cmdMatcher.group(1);
		return true;
	}

	@Override
	public final String execute(ResourceDirectory res_dir) {

		if (mode.equals("on")) {
			SmartLights.enable();
			return "Smart lights management enabled";
		} else if (mode.equals("off")) {
			SmartLights.disable();
			return "Smart lights management disabled";
		} else if (mode.equals("status")) {
			return String.format("Smart lights: %s", (SmartLights.isEnabled() ? "enabled" : "disabled"));
		} else {
			return "Smart lights: unknown setting";
		}
	}

	public static String help() {

		return "smartlights - Enable/disable smart lights management";
	}

	public static String getKeyword() {

		return "smartlights";
	}
}
