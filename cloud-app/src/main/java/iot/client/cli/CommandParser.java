package iot.client.cli;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import iot.client.cli.commands.ClearCommand;
import iot.client.cli.commands.HelpCommand;
import iot.client.cli.commands.LsCommand;
import iot.client.cli.commands.ReadCommand;
import iot.client.cli.commands.SetCommand;
import iot.client.cli.commands.SmartlightsCommand;
import iot.client.cli.commands.SmarttempCommand;

/**
 * A CommandParser interpretes the text inside the messages coming from the
 * user, identifies the corresponding command and parses its arguments.
 */
public class CommandParser {

	private static ArrayList<Class<? extends Command>> commands;

	static {
		commands = new ArrayList<>();
		commands.add(HelpCommand.class);
		commands.add(LsCommand.class);
		commands.add(ClearCommand.class);
		commands.add(ReadCommand.class);
		commands.add(SetCommand.class);
		commands.add(SmartlightsCommand.class);
		commands.add(SmarttempCommand.class);
	}

	/**
	 * Get a list of supported commands
	 * 
	 * @return List of classes, where each class is one of the supported commands
	 */
	public static ArrayList<Class<? extends Command>> getSupportedCommands() {

		return commands;
	}

	/**
	 * Reads the input string looking for a valid command, then returns the
	 * appropriate instance of Command if the syntax is correct.
	 * 
	 * @param text Raw text of the command
	 * @return the parsed command
	 */
	public Command getCommand(String text) {

		Command parsedCommand = null; // return variable

		// Iterate over the list of available commands to find the right one
		for (Class<? extends Command> cmd : commands) {
			try {
				// Get the method to verify the signature of this specific Command
				Method m = cmd.getMethod("matchesCommandSignature", String.class);

				// Check if the input text matches that signature
				boolean signatureMatches = (Boolean) m.invoke(null, text);

				// If it matches, get the constructor and create a new instance
				if (signatureMatches) {
					Constructor<? extends Command> ctr = cmd.getConstructor(String.class);
					parsedCommand = (Command) ctr.newInstance(text);
					break;
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				System.err.println("You probably forgot to implement method "
						+ "'matchesCommandSignature' in the class " + cmd.getName());
			} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException
					| InstantiationException e) {
				e.printStackTrace();
			}
		}
		return parsedCommand;
	}
}
