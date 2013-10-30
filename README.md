RESTClient
==========

Android async REST client helper library project. Simple to use, with callback methods.

To use the helper class you must implement RESTClientResponseHandler and set activity context to RESTClient.

Example:
``` java
RESTClient client = new RESTClient();
try {
	client._context = this;
	client._handler = new RESTClientResponseHandler() {
		@Override
		public void onSuccess(JSONObject responseData) {
			System.out.println(responseData);
		}
		
		@Override
		public void onFailure(FailStatus error) {
			System.out.println(error.toString());
		}
	};
	client.execute(new URI("http://www.domainname.something/api/login"));
} catch (URISyntaxException e) {
	e.printStackTrace();
}
```

Includes:
- asyc htpp calls
- simple callback handler, which is easy to customize

TODO:
- handle timeout
