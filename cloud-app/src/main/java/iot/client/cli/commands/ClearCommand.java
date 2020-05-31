package iot.client.cli.commands;

import iot.client.ResourceDirectory;
import iot.client.cli.Command;
import iot.client.regex.RegexBox;

import java.util.regex.Matcher;

public class ClearCommand extends Command {

	public ClearCommand(String text) {
		super(text);
		if (!matchesCommandSignature(text))
			throw new IllegalArgumentException();
	}

	public static boolean matchesCommandSignature(String text) {
		final Matcher m = RegexBox.CLEAR_PATTERN.matcher(text);
		return m.matches();
	}

	@Override
	protected final boolean parseArgs() {
		return true;
	}

	@Override
	public final String execute(ResourceDirectory res_dir) {
		// Clean the console
		System.out.print("\033[H\033[2J");
		System.out.flush();
		return " ";
	}

	public static String help() {

		return "clear - Clear the console output";
	}

	public static String getKeyword() {

		return "clear";
	}
}
