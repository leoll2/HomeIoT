package iot.client;

public abstract class Command {
	
    protected String commandText;   //the full command text
    
    protected Command(String text) {
        this.commandText = text;
    }
    
    /**
     * Verifies that the arguments of the command make sense and are not inconsistent
     * @return 
     *      true if arguments are valid, false otherwise
     */
    protected abstract boolean validateArgsConsistency();

    /**
     * Reads and validates the arguments passed to a command.
     * @return 
     *      true if arguments were successfully parsed, false otherwise
     */
    protected abstract boolean parseArgs();
    
    /**
     * Executes the command
     * @return
     *      a String containing the output of the executed command, null if 
     *      an unexpected error occurred
     */
    protected abstract String execute(CloudAppCoapClient app_coap_client,
    								  ResourceDirectory res_dir);
}
