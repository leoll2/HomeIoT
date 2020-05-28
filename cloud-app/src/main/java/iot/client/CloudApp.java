package iot.client;

import java.util.Scanner;

import org.eclipse.californium.core.CaliforniumLogger;

import iot.client.smartfeatures.SmartLights;


public class CloudApp {
	
	private ResourceDirectory res_dir;
	private Thread res_dir_t;
	private SmartLights smart_lights;
	private Thread smart_lights_t;
	private CommandParser comm_parser;
	
	
	// Constructor
	public CloudApp() {
		
		res_dir = ResourceDirectory.getResourceDirectory();
		res_dir_t = new Thread(res_dir);
		res_dir_t.start();
		
		smart_lights = SmartLights.getSmartLights(res_dir);
		smart_lights_t = new Thread(smart_lights);
		smart_lights_t.start();
		
		comm_parser = new CommandParser();
	}
	
	
	public static void main(String[] args) {
		
		CaliforniumLogger.disableLogging();
		
		CloudApp app = new CloudApp();
		Scanner cmd = new Scanner(System.in);
		
	    // Wait a bit for initialization
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Clean the console
		System.out.print("\033[H\033[2J");  
	    System.out.flush();
	    
	    // Show ASCII art intro message
	    ASCIIArtGenerator art_gen = new ASCIIArtGenerator();
	    try {
			art_gen.printTextArt("HomeIoT", ASCIIArtGenerator.ART_SIZE_SMALL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Fetch, decode and execute commands in loop
		while (true) {
			// Parse a command
			System.out.print("> ");	  
	        String s = cmd.nextLine();
	        Command command = app.comm_parser.getCommand(s);
	        
	        // If the syntax was valid and we got a Command
            if (command != null) {
            	// Try to execute it
                String result = command.execute(app.res_dir);

                // Display response
                if (result == null)
                	// If the execution failed for any reason, return
                    return;                
                else if (result.isEmpty())
                	// If the command succeeded with empty output, print 'Done'
                    System.out.println("Done");                
                else
                	// If the command succeeded with non-empty output, print that output
                	System.out.println(result);
            }
		}
	}
}
