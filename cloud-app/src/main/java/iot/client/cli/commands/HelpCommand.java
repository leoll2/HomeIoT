package iot.client.cli.commands;

import iot.client.ResourceDirectory;
import iot.client.cli.Command;
import iot.client.cli.CommandParser;
import iot.client.regex.RegexBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;

public class HelpCommand extends Command {

	protected enum HelpType {
		TYPE1, TYPE2
	};

	private HelpType type;
	private String cmd_arg = "";

	public HelpCommand(String text) {
		super(text);
		if (!matchesCommandSignature(text))
			throw new IllegalArgumentException();
		parseArgs();
	}

	public static boolean matchesCommandSignature(String text) {
		final Matcher m1 = RegexBox.HELP_T1_PATTERN.matcher(text);
		final Matcher m2 = RegexBox.HELP_T2_PATTERN.matcher(text);
		return m1.matches() || m2.matches();
	}

	@Override
	protected final boolean parseArgs() {

		if (RegexBox.HELP_T2_PATTERN.matcher(this.commandText).matches()) {
			// "help" + <cmd>
			type = HelpType.TYPE2;
			final Matcher cmdMatcher = RegexBox.HELP_T2_PATTERN.matcher(this.commandText);
			cmdMatcher.find();
			this.cmd_arg = cmdMatcher.group(1);
		} else {
			type = HelpType.TYPE1;
		}
		return true;
	}

	@Override
	public final String execute(ResourceDirectory res_dir) {

		String out_str = (type == HelpType.TYPE1 ? "Available commands:\n" : "Command not found");
		for (Class<? extends Command> cmd : CommandParser.getSupportedCommands()) {
			try {
				// Get the method to verify the signature of this specific Command
				Method m = cmd.getMethod("getKeyword");
				// Obtain the command keyword
				String keyword = (String) m.invoke(null);

				if (type == HelpType.TYPE1) {
					out_str = out_str.concat("  - " + keyword + '\n');
				} else if (type == HelpType.TYPE2 && keyword.equals(cmd_arg)) {
					m = cmd.getMethod("help");
					out_str = (String) m.invoke(null);
					break;
				}
			} catch (NoSuchMethodException e) {
				System.err.println(
						"You probably forgot to implement method " + "'getKeyword' in the class " + cmd.getName());
			} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		if (type == HelpType.TYPE1)
			out_str = out_str.concat("Use 'help <cmd>' to learn more about a specific command");

		return out_str;
	}

	public static String help() {

		return "Help command";
	}

	public static String getKeyword() {

		return "help";
	}
}
