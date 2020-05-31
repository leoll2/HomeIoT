package iot.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class ResourceCoapClient extends CoapClient implements Runnable {

	private String res_uri; // full URI to locate the resource univocally
	private ConstrainedDeviceResource cdr; // reference to the associate ConstrainedDeviceResource
	private CoapClient post_client; // client for post requests
	private CoapClient obs_client; // client for observing

	/**
	 * Do nothing
	 */
	private void idle() {

		while (true) {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				// nothing
			}
		}
	}

	/**
	 * Send a POST request with given payload to the CoAP server
	 * 
	 * @param payload content of the POST request
	 * @return true if success, false otherwise
	 */
	public Boolean doPost(String payload) {

		CoapResponse response = post_client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
		return ResponseCode.isSuccess(response.getCode());
	}

	/* Constructor */
	public ResourceCoapClient(String ip, String path, ConstrainedDeviceResource cdr) {

		this.res_uri = String.format("coap://[%s]:5683/%s", ip, path);
		this.cdr = cdr;

		post_client = new CoapClient(this.res_uri);
		obs_client = new CoapClient(this.res_uri);
	}

	@Override
	public void run() {

		// CoAP observing
		CoapObserveRelation obs_rel = this.obs_client.observe(new CoapHandler() {
			public void onLoad(CoapResponse resp) {
				String obs_resp_text = resp.getResponseText();
				cdr.update(obs_resp_text);
			}

			public void onError() {
				System.err.println("CoAP observer: Request timeout");
			}
		});

		// Wait indefinitely
		idle();

		obs_rel.proactiveCancel();
	}
}
