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

import org.json.JSONObject;

/**
 * Interface for RESTClient callback methods. For successful usage of RESTClient you
 * need to override methods and send RESTClient instance of this class.
 * @author Nejc Males
 */
public interface RESTClientResponseHandler {
	/**
	 * This are status codes for errors that RESTClient handles.
	 */
	public enum FailStatus {
	   NoNetworkConnection,
	   CallTimeout,
	   IOException,
	   ClientProtocolException,
	   JSONException
	}
	
	/**
	 * Callback method that is called when request finished(what was the response data
	 * does not matter).
	 * @param Response data returned by request in JSON format.
	 */
	public void onSuccess(JSONObject responseData);
	
	/**
	 * Callback method that is called when request did not finish or there was any other
	 * error/exception. 
	 * @param Error code
	 */
	public void onFailure(FailStatus error);
}
