/**
 * Copyright Nejc Males
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helper.util;

import helper.util.RESTClientResponseHandler.FailStatus;

import java.io.IOException;
import java.net.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;

/**
 * Class implements basic asynchronous REST client.
 * Using AsyncTask enables usage of asynchronous calls, so our main thread doesnt get
 * blocked. In order to successfully use this class user must implement 
 * RESTClientResponseHandler. Class could be used for any http request that has JSON 
 * object as response.
 * @author Nejc Males
 */
public class RESTClient extends AsyncTask<URI, Void, Void> {
	/**
	 * Must be set from calling activity, for callback methods.
	 */
	public RESTClientResponseHandler _handler;
	/**
	 * Must be set from calling activity, for network availability check.
	 */
	public Context _context;
	
	/**
	 * Main method for server request. Depending on call status, the callback methods are
	 * invoked.
	 * @param URI full address from which we need to get response.
	 */
    @Override
    protected Void doInBackground(URI... uris) {
    	if(! networkAvailable()) {
    		System.out.println("no network connection");
    		_handler.onFailure(FailStatus.NoNetworkConnection);
    		return null;
    	}
    	
    	HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(uris[0]);
		try {
			// start http request
			HttpResponse response = httpclient.execute(httpget);
			// get response string
			String responseData = EntityUtils.toString(response.getEntity());
			JSONObject responseJSON = responseToJSONObject(responseData);
			if(responseJSON == null) {
				_handler.onFailure(FailStatus.JSONException);
			}
			else {
				_handler.onSuccess(responseJSON);
			}
		} catch (ClientProtocolException e) {
			System.out.println("AsyncTask:GetUserDataThread:ClientProtocolException: "
							+ e.getMessage());
			_handler.onFailure(FailStatus.ClientProtocolException);
		} catch (IOException e) {
			System.out.println("AsyncTask:GetUserDataThread:IOException: "
							+ e.getMessage());
			_handler.onFailure(FailStatus.IOException);
		}
		
		return null;
    }
    
    /**
     * Helper function for checking of network availability. Class must have initialized
     * _context, otherwise it will not work.
     * @return boolean status of network connection
     */
	private boolean networkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Helper function for string to JSONObject parsing.
	 * @param string that needs to be parsed
	 * @return JSONObject representation of given string
	 */
	private JSONObject responseToJSONObject(String responseData) {
		JSONObject _jsonObject = null;
		try {
			_jsonObject = new JSONObject(responseData);
		} catch (JSONException e) {
			System.out.println("AsyncTask:UserLoginThread:JSONException: "
							+ e.getMessage());
		}
		return _jsonObject;
	}
}
