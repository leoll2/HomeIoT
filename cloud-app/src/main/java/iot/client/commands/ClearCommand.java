package iot.client.commands;

import iot.client.Command;
import iot.client.RegexBox;
import iot.client.ResourceDirectory;

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
    protected final boolean validateArgsConsistency(){
        return true;
    }
    
    @Override
    protected final boolean parseArgs() {
        return true;
    }
    
    @Override
    protected final String execute(ResourceDirectory res_dir) {
    	// Clean the console
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();
    	return " ";
    }
}
