package iot.client.commands;

import iot.client.Command;
import iot.client.ConstrainedDeviceResource;
import iot.client.RegexBox;
import iot.client.ResourceDirectory;

import java.util.List;
import java.util.regex.Matcher;

public class SetCommand extends Command {

	protected enum SetType { TYPE1, TYPE2, TYPE3, TYPE4 };
	
	private SetType type;
	private String ip = "";
	private String path = "";
	private String rt = "";
	private String val = "";
	
    public SetCommand(String text) {
        super(text);
        if (!matchesCommandSignature(text))
            throw new IllegalArgumentException();
        parseArgs();
    }
    
    public static boolean matchesCommandSignature(String text) {
        final Matcher m1 = RegexBox.SET_T1_PATTERN.matcher(text);
        final Matcher m2 = RegexBox.SET_T2_PATTERN.matcher(text);
        final Matcher m3 = RegexBox.SET_T3_PATTERN.matcher(text);
        final Matcher m4 = RegexBox.SET_T4_PATTERN.matcher(text);
        return m1.matches() || m2.matches() || m3.matches() || m4.matches();
    }
    
    @Override
    protected final boolean validateArgsConsistency() {
        return true;
    }
    
    @Override
    protected final boolean parseArgs() {
    	
    	if (RegexBox.SET_T1_PATTERN.matcher(this.commandText).matches()) {
    		// "set" + <ip> + <path>
    		type = SetType.TYPE1;
    		final Matcher cmdMatcher = RegexBox.SET_T1_PATTERN.matcher(this.commandText);
    		cmdMatcher.find();
    		this.ip = cmdMatcher.group(1);
    		this.path = cmdMatcher.group(4);
    	} else if (RegexBox.SET_T2_PATTERN.matcher(this.commandText).matches()) {
    		// "set" + <ip> + <path> + <val>
    		type = SetType.TYPE2;
    		final Matcher cmdMatcher = RegexBox.SET_T2_PATTERN.matcher(this.commandText);
    		cmdMatcher.find();
    		this.ip = cmdMatcher.group(1);
    		this.path = cmdMatcher.group(4);
    		this.val = cmdMatcher.group(5);
    	} else if (RegexBox.SET_T3_PATTERN.matcher(this.commandText).matches()) {
    		// "set" + <type>
    		type = SetType.TYPE3;
    		final Matcher cmdMatcher = RegexBox.SET_T3_PATTERN.matcher(this.commandText);
    		cmdMatcher.find();
    		this.rt = cmdMatcher.group(1);
    	} else if (RegexBox.SET_T4_PATTERN.matcher(this.commandText).matches()) {
    		// "set" + <type> + <val>
    		type = SetType.TYPE4;
    		final Matcher cmdMatcher = RegexBox.SET_T4_PATTERN.matcher(this.commandText);
    		cmdMatcher.find();
    		this.rt = cmdMatcher.group(1);
    		this.val = cmdMatcher.group(2);
    	}
    	return true;
    }
    
    @Override
    protected final String execute(ResourceDirectory res_dir) {
    	
    	ConstrainedDeviceResource res;
    	List<ConstrainedDeviceResource> ress;
    	String s;
    	
    	switch (type) {
    	case TYPE1:
    		System.out.println(String.format("Setting coap://[%s]:5683/%s ...", this.ip, this.path));
    		res = res_dir.findResourceByIpPath(this.ip, this.path);
			if (res == null)
				return "Resource not available\n";
			else
				return res.doSet() + '\n';
    	case TYPE2:
    		System.out.println(String.format("Setting coap://[%s]:5683/%s to value %s ...", this.ip, this.path, this.val));
    		res = res_dir.findResourceByIpPath(this.ip, this.path);
			if (res == null)
				return "Resource not available\n";
			else
				return res.doSet(this.val) + '\n';
    	case TYPE3:
    		System.out.println(String.format("Setting resources of type %s ...", this.rt));
    		s = "";
			ress = res_dir.findResourcesByType(this.rt);
			for (ConstrainedDeviceResource r : ress) {
				s = s.concat(String.format("coap://[%s]:5683/%s: %s\n", r.getIp(), r.getPath(), r.doSet())); 
			}
			return (s.length() > 0 ? s : String.format("No resource available of type %s\n", this.rt));
    	case TYPE4:
    		System.out.println(String.format("Setting resources of type %s to value %s ...", this.rt, this.val));
    		s = "";
			ress = res_dir.findResourcesByType(this.rt);
			for (ConstrainedDeviceResource r : ress) {
				s = s.concat(String.format("coap://[%s]:5683/%s: %s\n", r.getIp(), r.getPath(), r.doSet(this.val))); 
			}
			return (s.length() > 0 ? s : String.format("No resource available of type %s\n", this.rt));
    	default:
    		return "set: invalid format";
    	}
    }
}