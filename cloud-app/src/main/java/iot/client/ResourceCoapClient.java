package iot.client;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;


public class ResourceCoapClient extends CoapClient implements Runnable {

	/*
	CoapResponse response;
	String url="coap://[fd00::203:3:3:3]:5683/lightbulb";
    URI uri= null;
    try {
        uri = new URI(url);
    } catch (URISyntaxException e) {
        e.printStackTrace();
    }

	CoapClient client = new CoapClient(uri);

	// GET
	Request request = new Request(CoAP.Code.GET);
	request.setOptions((new OptionSet()).setAccept(MediaTypeRegistry.TEXT_PLAIN));
	response = client.advanced(request);
	System.out.print("Get response code: " + response.getCode()+"\n");
	System.out.print(response.getResponseText()+"\n");

	*/
	private String res_uri;					// full URI to locate the resource univocally
	private ConstrainedDeviceResource cdr;	// reference to the associate ConstrainedDeviceResource
	private CoapClient post_client;			// client for post requests		// TODO si può unificare?
	private CoapClient obs_client;			// client for observing
	
	
	private void idle() {
		
		while(true) {
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				// nothing
			}
		}
	}
	
	
	public Boolean doPost(String payload) {
		
		CoapResponse response = post_client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
		return ResponseCode.isSuccess(response.getCode());
	}


	// Constructor
	public ResourceCoapClient(String ip, String path, ConstrainedDeviceResource cdr) {
		
		this.res_uri = String.format("coap://[%s]:5683/%s", ip, path);
		this.cdr = cdr;
		
		post_client = new CoapClient(this.res_uri);
		obs_client = new CoapClient(this.res_uri);
	}
	
	
	@Override
	public void run() {
		
		// CoAP observing
		CoapObserveRelation obs_rel = this.obs_client.observe(
				new CoapHandler() {
					public void onLoad(CoapResponse resp) {
						String obs_resp_text = resp.getResponseText();
						cdr.update(obs_resp_text);
					}
					public void onError() {
						System.err.println("Failed");
					}
				}
		);
		
		idle();
		
		obs_rel.proactiveCancel(); // stop observing
	}
}
