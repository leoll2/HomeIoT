package iot.client.commands;

import iot.client.ResourceCoapClient;
import iot.client.Command;
import iot.client.RegexBox;
import iot.client.ResourceDirectory;

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
    protected final boolean validateArgsConsistency(){
        return true;
    }
    
    @Override
    protected final boolean parseArgs() {
        return true;
    }
    
    @Override
    protected final String execute(ResourceDirectory res_dir) {
    	return res_dir.toString();
    }
}
