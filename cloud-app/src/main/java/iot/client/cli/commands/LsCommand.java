package iot.client.cli.commands;

import iot.client.ResourceDirectory;
import iot.client.cli.Command;
import iot.client.regex.RegexBox;

import java.util.regex.Matcher;

public class LsCommand extends Command {

	public LsCommand(String text) {
		super(text);
		if (!matchesCommandSignature(text))
			throw new IllegalArgumentException();
	}

	public static boolean matchesCommandSignature(String text) {
		final Matcher m = RegexBox.LS_PATTERN.matcher(text);
		return m.matches();
	}

	@Override
	protected final boolean parseArgs() {
		return true;
	}

	@Override
	public final String execute(ResourceDirectory res_dir) {
		return res_dir.toString();
	}

	public static String getKeyword() {

		return "ls";
	}

	public static String help() {

		return "ls - Show all the currently registered resources.";
	}
}
