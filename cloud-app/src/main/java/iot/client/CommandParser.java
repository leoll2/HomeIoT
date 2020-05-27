package iot.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import iot.client.commands.LsCommand;
import iot.client.commands.ReadCommand;

/**
 * A CommandParser interpretes the text inside the messages coming from the user,
 * identifies the corresponding command and parses its arguments.
 */
public class CommandParser {

    private ArrayList<Class<? extends Command>> commands;
    
    private void loadCommands() {
        commands = new ArrayList<>();
        commands.add(LsCommand.class);
        commands.add(ReadCommand.class);
    }
    
    /**
     * Creates a new instance of CommandParser
     */
    public CommandParser() {
        loadCommands();
    }
    
    /**
     * Reads the input string looking for a valid command, then returns the 
     * appropriate instance of Command if the syntax is correct.
     * 
     * @param text
     *      Raw text of the command
     * @return
     *      the parsed command
     */
    public Command getCommand(String text) {
        
        Command parsedCommand = null;       // return variable
        
        // Iterate over the list of available commands to find the right one
        for (Class<? extends Command> cmd : commands) {
            try {
                // Get the method to verify the signature of this specific Command
                Method m = cmd.getMethod("matchesCommandSignature", String.class);
                
                // Check if the input text matches that signature
                boolean signatureCorresponds = (Boolean)m.invoke(null, text);
               
                // If it corresponds, get the constructor and create a new instance
                if (signatureCorresponds) {
                    Constructor ctr = cmd.getConstructor(String.class);
                    parsedCommand = (Command)ctr.newInstance(text);
                    break;
                }
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
                System.err.println("You probably forgot to implement method "
                                  + "'matchesCommandSignature' in the class " 
                                  + cmd.getName());
            } catch (IllegalAccessException   | InvocationTargetException |
                    IllegalArgumentException | InstantiationException e) {
               e.printStackTrace();
           }
        }
        return parsedCommand;
    }
}
