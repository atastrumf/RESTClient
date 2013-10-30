package helper.util;

import org.json.JSONObject;

public interface RESTClientResponseHandler {
	public enum FailStatus {
	   NoNetworkConnection,
	   CallTimeout,
	   IOException,
	   ClientProtocolException,
	   JSONException
	}
	
	public void onSuccess(JSONObject responseData);
	
	public void onFailure(FailStatus error);
}
