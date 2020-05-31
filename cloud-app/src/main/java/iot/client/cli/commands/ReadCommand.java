package iot.client.cli.commands;

import iot.client.ConstrainedDeviceResource;
import iot.client.ResourceDirectory;
import iot.client.cli.Command;
import iot.client.regex.RegexBox;

import java.util.List;
import java.util.regex.Matcher;

public class ReadCommand extends Command {

	protected enum ReadType {
		TYPE1, TYPE2
	};

	private ReadType type;
	private String ip = "";
	private String path = "";
	private String rt = "";

	public ReadCommand(String text) {
		super(text);
		if (!matchesCommandSignature(text))
			throw new IllegalArgumentException();
		parseArgs();
	}

	public static boolean matchesCommandSignature(String text) {
		final Matcher m1 = RegexBox.READ_T1_PATTERN.matcher(text);
		final Matcher m2 = RegexBox.READ_T2_PATTERN.matcher(text);
		return m1.matches() || m2.matches();
	}

	@Override
	protected final boolean parseArgs() {

		if (RegexBox.READ_T1_PATTERN.matcher(this.commandText).matches()) {
			// "read" + <ip> + <path>
			type = ReadType.TYPE1;
			final Matcher cmdMatcher = RegexBox.READ_T1_PATTERN.matcher(this.commandText);
			cmdMatcher.find();
			this.ip = cmdMatcher.group(1);
			this.path = cmdMatcher.group(4);
		} else if (RegexBox.READ_T2_PATTERN.matcher(this.commandText).matches()) {
			// "read" + <type>
			type = ReadType.TYPE2;
			final Matcher cmdMatcher = RegexBox.READ_T2_PATTERN.matcher(this.commandText);
			cmdMatcher.find();
			this.rt = cmdMatcher.group(1);
		}
		return true;
	}

	@Override
	public final String execute(ResourceDirectory res_dir) {
		switch (type) {
		case TYPE1:
			// "read" + <ip> + <path>
			System.out.println(String.format("Querying coap://[%s]:5683/%s ...", this.ip, this.path));
			ConstrainedDeviceResource res = res_dir.findResourceByIpPath(this.ip, this.path);
			if (res == null)
				return "Resource not available";
			else
				return res.doRead();
		case TYPE2:
			// "read" + <type>
			System.out.println(String.format("Querying resources of type %s ...", this.rt));
			String s = "";
			List<ConstrainedDeviceResource> ress = res_dir.findResourcesByType(this.rt);
			for (ConstrainedDeviceResource r : ress) {
				s = s.concat(r.getFullPath() + " -> " + r.doRead());
			}
			return (s.length() > 0 ? s : String.format("No resource available of type %s", this.rt));
		default:
			return "read: invalid format";
		}
	}

	public static String getKeyword() {

		return "read";
	}

	public static String help() {

		return "read - Get the state of a resource or a group of them\n" + "Syntax: \n" + " - read <ip> <path>\n"
				+ " - read <type>\n";
	}
}
