package iot.client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class ResourceCoapClient extends CoapClient implements Runnable {

	private String res_uri; // full URI to locate the resource univocally
	private ConstrainedDeviceResource cdr; // reference to the associate ConstrainedDeviceResource
	private CoapClient client; // client for observing
	private Lock l;
	private Condition shouldStop;

	/**
	 * Send a POST request with given payload to the CoAP server
	 * 
	 * @param payload content of the POST request
	 * @return true if success, false otherwise
	 */
	public Boolean doPost(String payload) {

		CoapResponse response = client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
		return ResponseCode.isSuccess(response.getCode());
	}

	/**
	 * Signal the thread associated to the resource to stop as soon as possible
	 */
	public void stop() {

		l.lock();
		shouldStop.signal();
		l.unlock();
	}

	/* Constructor */
	public ResourceCoapClient(String ip, String path, ConstrainedDeviceResource cdr) {

		this.res_uri = String.format("coap://[%s]:5683/%s", ip, path);
		this.cdr = cdr;

		l = new ReentrantLock();
		shouldStop = l.newCondition();

		client = new CoapClient(this.res_uri);
	}

	@Override
	public void run() {

		// CoAP observing
		CoapHandler handler = new CoapHandler() {
			public void onLoad(CoapResponse resp) {
				String obs_resp_text = resp.getResponseText();
				cdr.update(obs_resp_text);
			}

			public void onError() {
				// System.err.println("CoAP observer: Request timeout");
			}
		};
		CoapObserveRelation obs_rel = this.client.observe(handler);

		// Wait until stopped
		l.lock();
		try {
			shouldStop.await();
		} catch (Exception e) {
		}
		l.unlock();

		obs_rel.reactiveCancel();
	}
}
