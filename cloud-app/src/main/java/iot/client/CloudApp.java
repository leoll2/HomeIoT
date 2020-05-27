package iot.client;

import java.util.Scanner;


public class CloudApp {
	
	private CloudAppCoapClient app_coap_client;
	private ResourceDirectory res_dir;
	private Thread app_coap_client_t;
	private Thread res_dir_t;
	private CommandParser comm_parser;
	
	
	// Constructor
	public CloudApp() {
		
		System.out.println("HomeIoT");
		
		res_dir = new ResourceDirectory();
		res_dir_t = new Thread(res_dir);
		res_dir_t.start();
		
		app_coap_client = new CloudAppCoapClient(res_dir);
		app_coap_client_t = new Thread(app_coap_client);
		app_coap_client_t.start();
		
		comm_parser = new CommandParser();
	}
	
	
	public static void main(String[] args) {
		
		CloudApp app = new CloudApp();
		Scanner cmd = new Scanner(System.in);
		
		while (true) {
			// Parse a command
			System.out.print("> ");	  
	        String s = cmd.nextLine();
	        Command command = app.comm_parser.getCommand(s);
	        
	        // If the syntax was valid and we got a Command
            if (command != null) {
            	// Try to execute it
                String result = command.execute(app.app_coap_client, app.res_dir);

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
