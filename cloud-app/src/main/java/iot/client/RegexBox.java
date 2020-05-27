package iot.client;

import java.util.regex.Pattern;

public class RegexBox {

    // Regex patterns
    public static final Pattern 
            NUMBER_PATTERN,             // 2 decimals positive number
            IPV6_STD_PATTERN,			// ipv6 uncompressed pattern
            IPV6_COMPR_PATTERN,			// ipv6 compressed pattern
            IPV6_PATTERN,				// ipv6 pattern
            COAP_RESOURCE_PATH_PATTERN,	// CoAP resource pattern
            COAP_RESOURCE_TYPE_PATTERN, // CoAP resource type pattern
            LS_PATTERN,           		// keyword "ls"
            READ_T1_PATTERN,			// keyword "read" + <ip> + <path>
            READ_T2_PATTERN;			// keyword "read" + <type>
    
    // Initialize and compile the regular expressions
    static {
        String 
        numberPatternString =
                "(?!(?:0|0\\.0|0\\.00)$)[+]?\\d+(\\.\\d{1,2})?",
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
        lsPatternString = 
                "^\\s*ls\\s*$",
        readT1PatternString = 
                "^\\s*read" + "\\s+" + ipv6PatternString + "\\s+" + coapResourcePathPatternString + "\\s*" + "$",
        readT2PatternString = 
                "^\\s*read" + "\\s+" + coapResourceTypePatternString + "\\s*" + "$";
        System.out.println(readT1PatternString);
    
	    NUMBER_PATTERN = Pattern.compile(numberPatternString);
	    IPV6_STD_PATTERN = Pattern.compile(ipv6StdPatternString);
	    IPV6_COMPR_PATTERN = Pattern.compile(ipv6HexCompressedPatternString);
	    IPV6_PATTERN = Pattern.compile(ipv6PatternString);
	    COAP_RESOURCE_PATH_PATTERN = Pattern.compile(coapResourcePathPatternString);
	    COAP_RESOURCE_TYPE_PATTERN = Pattern.compile(coapResourceTypePatternString);
	    LS_PATTERN = Pattern.compile(lsPatternString);
	    READ_T1_PATTERN = Pattern.compile(readT1PatternString);
	    READ_T2_PATTERN = Pattern.compile(readT2PatternString);
    }
}
