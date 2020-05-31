package iot.client.cli;

import iot.client.ResourceDirectory;

public abstract class Command {

	protected String commandText; // full command (text) when it is invoked

	protected Command(String text) {
		this.commandText = text;
	}

	/**
	 * Reads and validates the arguments passed to a command.
	 * 
	 * @return true if arguments were successfully parsed, false otherwise
	 */
	protected abstract boolean parseArgs();

	/**
	 * Executes the command
	 * 
	 * @return a String containing the output of the executed command, null if an
	 *         unexpected error occurred
	 */
	public abstract String execute(ResourceDirectory res_dir);
}
