package iot.client;

import java.util.regex.Pattern;

public class RegexBox {

    // Regex patterns
    public static final Pattern 
            INTEGER_PATTERN,            // integer number
            ALPHANUMSYMB_PATTERN,		// mix of alphanumeric chars and symbols
            IPV6_STD_PATTERN,			// ipv6 uncompressed pattern
            IPV6_COMPR_PATTERN,			// ipv6 compressed pattern
            IPV6_PATTERN,				// ipv6 pattern
            COAP_RESOURCE_PATH_PATTERN,	// CoAP resource pattern
            COAP_RESOURCE_TYPE_PATTERN, // CoAP resource type pattern
            HELP_PATTERN,				// keyword "help"
            LS_PATTERN,           		// keyword "ls"
            CLEAR_PATTERN,				// keyword "clear"
            READ_T1_PATTERN,			// keyword "read" + <ip> + <path>
            READ_T2_PATTERN,			// keyword "read" + <type>
    		SET_T1_PATTERN,				// keyword "set" + <ip> + <path>
    		SET_T2_PATTERN,				// keyword "set" + <ip> + <path> + <value>
    		SET_T3_PATTERN,				// keyword "set" + <type>
    		SET_T4_PATTERN;				// keyword "set" + <type> + <value>
    
    // Initialize and compile the regular expressions
    static {
        String 
        integerPatternString =
                "(\\d+?)",
        alphanumsymbPatternString =
        		"([-a-zA-Z0-9+&@#/%?=~_]+)",
        ipv6StdPatternString = 
        		"(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}",
        ipv6HexCompressedPatternString =
        		"((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)",
        ipv6PatternString =
        		"(" + ipv6StdPatternString + "|" + ipv6HexCompressedPatternString + ")",
        coapResourcePathPatternString =
        		"([-a-zA-Z0-9+&@#/%?=~_]+)",
        coapResourceTypePatternString =
        		"([-a-zA-Z0-9+&@#/%?=~_]+)",
        helpPatternString = 
        		"^\\s*help\\s*$",
        lsPatternString = 
                "^\\s*ls\\s*$",
        clearPatternString = 
                "^\\s*clear\\s*$",
        readT1PatternString = 
                "^\\s*read" + "\\s+" + ipv6PatternString + "\\s+" + coapResourcePathPatternString + "\\s*" + "$",
        readT2PatternString = 
                "^\\s*read" + "\\s+" + coapResourceTypePatternString + "\\s*" + "$",
        setT1PatternString =
        		"^\\s*set" + "\\s+" + ipv6PatternString + "\\s+" + coapResourcePathPatternString + "\\s*" + "$",
        setT2PatternString =
        		"^\\s*set" + "\\s+" + ipv6PatternString + "\\s+" + coapResourcePathPatternString + "\\s+"
        		+ integerPatternString + "\\s*" + "$",
        setT3PatternString =
        		"^\\s*set" + "\\s+" + coapResourceTypePatternString + "\\s*" + "$",
        setT4PatternString =
        		"^\\s*set" + "\\s+" + coapResourceTypePatternString + "\\s+"
        		+ alphanumsymbPatternString + "\\s*" + "$";
    
	    INTEGER_PATTERN = Pattern.compile(integerPatternString);
	    ALPHANUMSYMB_PATTERN = Pattern.compile(alphanumsymbPatternString);
	    IPV6_STD_PATTERN = Pattern.compile(ipv6StdPatternString);
	    IPV6_COMPR_PATTERN = Pattern.compile(ipv6HexCompressedPatternString);
	    IPV6_PATTERN = Pattern.compile(ipv6PatternString);
	    COAP_RESOURCE_PATH_PATTERN = Pattern.compile(coapResourcePathPatternString);
	    COAP_RESOURCE_TYPE_PATTERN = Pattern.compile(coapResourceTypePatternString);
	    HELP_PATTERN = Pattern.compile(helpPatternString);
	    LS_PATTERN = Pattern.compile(lsPatternString);
	    CLEAR_PATTERN = Pattern.compile(clearPatternString);
	    READ_T1_PATTERN = Pattern.compile(readT1PatternString);
	    READ_T2_PATTERN = Pattern.compile(readT2PatternString);
	    SET_T1_PATTERN = Pattern.compile(setT1PatternString);
	    SET_T2_PATTERN = Pattern.compile(setT2PatternString);
	    SET_T3_PATTERN = Pattern.compile(setT3PatternString);
	    SET_T4_PATTERN = Pattern.compile(setT4PatternString);
    }
}
