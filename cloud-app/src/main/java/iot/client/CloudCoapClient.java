package iot.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;

// String url="coap://[fd00::202:2:2:2]:5683/led?color=r";

public class CloudCoapClient extends CoapClient {

	public static void main(String[] args) {
		
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

		// POST
		response = client.post("mode=on", MediaTypeRegistry.TEXT_PLAIN);
		System.out.print("Post response code: " + response.getCode()+"\n");
		System.out.print(response.getResponseText()+"\n");
		
		
		
		// OBSERVING
		CoapClient obs_client = new CoapClient("coap://[fd00::205:5:5:5]:5683/pir");
		CoapObserveRelation obs_rel = obs_client.observe(
				new CoapHandler() {
					public void onLoad(CoapResponse resp) {
						String content = "I was notified, got response: " + resp.getResponseText();
						System.out.println(content);
					}
					public void onError() {
						System.err.println("Failed");
					}
				}
		);
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			// nothings
		}
		obs_rel.proactiveCancel(); // stop observing
	}
}
