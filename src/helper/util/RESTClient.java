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
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.util.Log;

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
	 * Optional/additional parameters send along request. Must be set from calling activity.
	 */
	public List<NameValuePair> _params;
	
	/**
	 * Optional parameter that determines what kind of HTTP request method will be used.
	 * Possible values: GET, POST
	 */
	public String _methodType = "GET";
	
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
    	final HttpParams httpParameters = httpclient.getParams();

    	int connectionTimeOutSec = 10;
		HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeOutSec * 1000);
    	int socketTimeoutSec = 10;
		HttpConnectionParams.setSoTimeout        (httpParameters, socketTimeoutSec * 1000);
    	
		HttpRequestBase httpRequest = null;
		if(_methodType == "GET") {
			// check if we have any params
			if(_params != null) {
				String paramString = URLEncodedUtils.format(_params, "utf-8");
				httpRequest = new HttpGet(uris[0] + "?" + paramString);
				Log.d("request", uris[0] + "?" + paramString);
			}
			else {
				httpRequest = new HttpGet(uris[0]);
				Log.d("request", uris[0].toString());
			}
		}
		else {
			httpRequest = new HttpPost(uris[0]);
			if(_params != null) {
				Log.d("request", uris[0].toString());
				addPostParameters(httpRequest);
			}
		}
		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Content-Type", "application/json");
		try {
			// start http request
			HttpResponse response = httpclient.execute(httpRequest);
			// get response string
			String responseData = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			
			// determine response type(json array or object)
			Object json = null;
			try {
				json = new JSONTokener(responseData).nextValue();
				_handler.onSuccess(json);
			} catch (JSONException e) {
				_handler.onFailure(FailStatus.JSONException);
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
			System.out.println("AsyncTask:JSONException: "
							+ e.getMessage());
		}
		return _jsonObject;
	}
	
	/**
	 * Helper function for string to JSONArray parsing.
	 * @param string that needs to be parsed
	 * @return JSONArray representation of given string
	 */
	private JSONArray responseToJSONArray(String responseData) {
		JSONArray _jsonArray = null;
		try {
			_jsonArray = new JSONArray(responseData);
		} catch (JSONException e) {
			System.out.println("AsyncTask:JSONException: "
							+ e.getMessage());
		}
		return _jsonArray;
	}
	
	/**
	 * Helper function for adding POST parameters to request.
	 */
	private void addPostParameters(HttpRequestBase httpRequest)
	{
		if(_params == null) return;
		
		try {
			((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(_params));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
