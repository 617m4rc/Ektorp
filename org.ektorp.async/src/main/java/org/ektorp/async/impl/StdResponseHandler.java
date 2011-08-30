package org.ektorp.async.impl;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
/**
 * 
 * @author henrik
 *
 * @param <T>
 */
public class StdResponseHandler<T> implements AsyncHandler<T> {

	private AsyncResponseHandler<T> responseHandler;
	private final AsyncResponseHandler<T> successHandler;
	
	public StdResponseHandler(AsyncResponseHandler<T> successHandler) {
		this.successHandler = successHandler;
	}
	
	@Override
	public void onThrowable(Throwable t) {
		
	}

	@Override
	public com.ning.http.client.AsyncHandler.STATE onBodyPartReceived(
			HttpResponseBodyPart bodyPart) throws Exception {
		bodyPart.writeTo(responseHandler.getBodyStream());
		
		return STATE.CONTINUE;
	}

	@Override
	public com.ning.http.client.AsyncHandler.STATE onStatusReceived(
			HttpResponseStatus responseStatus) throws Exception {
		if (responseStatus.getStatusCode() < 300) {
			responseHandler = new AsyncErrorHandler<T>(responseStatus);
		} else {
			responseHandler = successHandler;
		}
		return com.ning.http.client.AsyncHandler.STATE.CONTINUE;
	}

	protected STATE onError(HttpResponseStatus responseStatus) {
		return null;
	}
	
	@Override
	public com.ning.http.client.AsyncHandler.STATE onHeadersReceived(
			HttpResponseHeaders headers) throws Exception {
		return com.ning.http.client.AsyncHandler.STATE.CONTINUE;
	}

	@Override
	public T onCompleted() throws Exception {
		return responseHandler.onCompleted();
	}

}