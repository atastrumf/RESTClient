RESTClient
==========

Android async REST client helper library project. Simple to use, with callback methods.

To use the helper class you must implement RESTClientResponseHandler and set activity context to RESTClient.
Default HTTP method is set to GET, but you can also use POST(see example). 

## Example
``` java
RESTClient client = new RESTClient();
try {
	client._context = this;
	client._methodType = "POST"; // or use GET

	// pass parameters as name-value pairs
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("username", "demo"));
	nameValuePairs.add(new BasicNameValuePair("password", "demo"));
	client._params = nameValuePairs;

	client._handler = new RESTClientResponseHandler() {
		@Override
		public void onSuccess(Object responseData) {
			if(((JSONObject) responseData).has("success")) {
				// let say that response has user id
				try {
					_userID = ((JSONObject) responseData).getString("user_id");
				} catch (JSONException e) {
					// maybe respose did not have "user_id".. :)
					e.printStackTrace();
				}

				// above code runs in seperate thread, to make changes on main thread use:
				runOnUiThread(new Runnable() {
				    public void run() {
				    	// do some fancy stuff on main UI-application thread
				    }
				});
			}
			else {
				Log.e(TAG, "login request does not have 'success' in response");
				try {
					Log.e(TAG, ((JSONObject) responseData).toString(4)); // outputs whole response
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		@Override
		public void onFailure(FailStatus error) {
			Log.e(TAG, error.toString());
		}
	};
	client.execute(new URI("http://www.domainname.something/api/login"));
} catch (URISyntaxException e) {
	e.printStackTrace();
} catch (Exception e) {
	e.printStackTrace();
}
```

Includes:
- asyc http calls
- simple callback handler, which is easy to customize
- handling of timeout
- GET and POST methods for passing parameters
- helper functions for:
    - transforming return data to JSON object(```responseToJSONObject()```) or JSON array(```responseToJSONArray()```)
    - determing network availability