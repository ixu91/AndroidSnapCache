package com.example.snapcache;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONStringer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class PostActivity extends Activity {

	String type = "";
	private static final String MY_API_KEY = "Alz5CvlnsTliNCseaYWgwz";
	String latitude = "";
	String longitude = "";
	String name = "";
	String fb_id = "";
	String uid = "";
	String url = "";
	String ftype = "";
	String privacy = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();

		latitude = b.getString("latitude");
		longitude = b.getString("longitude");
		type = b.getString("type");
		name = b.getString("name");
		fb_id = b.getString("fb_id");

		uid = b.getString("uid");
		url = b.getString("url");
		ftype = b.getString("file_type");
		privacy = b.getString("privacy");

		Log.i("POST", "we are in the post activity");

		if (type.equals("artifact")) {
			Log.i("TYPE", "we are posting a new artifact");

			HashMap<String, String> data = new HashMap<String, String>();
			data.put("data_type", ftype);
			data.put("file_url",
					url);
			data.put("latitude", latitude);
			data.put("longitude", longitude);
			data.put("name", "android_file");
			data.put("privacy", privacy);
			data.put("user_id", uid);
			Log.i("data",uid.toString());

			// data.put("radius", "100.0");
			AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);
			asyncHttpPost
					.execute("http://sheltered-falls-8280.herokuapp.com/artifacts.json");
		} else if (type.equals("user")) {
			Log.i("TYPE", "we are posting a new user");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("facebook_id", fb_id);
			data.put("name", name);
			AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);
			asyncHttpPost
					.execute("http://sheltered-falls-8280.herokuapp.com/users.json");
		}
	}

	public class AsyncHttpPost extends AsyncTask<String, String, String> {
		private HashMap<String, String> mData = null;// post data

		/**
		 * constructor
		 */
		public AsyncHttpPost(HashMap<String, String> data) {
			mData = data;
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;
			String str = "";
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0]
													// is URL
			try {
				int resCode;
				String res_string = "";

				// POST request to <service>/SaveVehicle
				HttpPost http_request = new HttpPost();
				if (type.equals("user")) {
					http_request = new HttpPost(
							"http://sheltered-falls-8280.herokuapp.com/users.json");
				} else if (type.equals("artifact")) {
					http_request = new HttpPost(
							"http://sheltered-falls-8280.herokuapp.com/artifacts.json");
				}

				http_request.setHeader("Content-type", "application/json");
				http_request.setHeader("user-agent", "Yoda");

				// Build JSON string
				JSONStringer vehicle = new JSONStringer();

				try {
					if (type.equals("user")) {
						vehicle = new JSONStringer().object()
								.key("facebook_id")
								.value(mData.get("facebook_id")).key("name")
								.value(mData.get("name")).endObject();

					} else if (type.equals("artifact")) {
						vehicle = new JSONStringer().object().key("data_type")
								.value(type).key("file_url")
								.value(mData.get("file_url")).key("latitude")
								.value(mData.get("latitude")).key("longitude")
								.value(mData.get("longitude")).key("name")
								.value(mData.get("name")).key("privacy")
								.value(mData.get("privacy")).key("user_id")
								.value(mData.get("user_id")).endObject();
					}
					StringEntity entity;
					try {
						entity = new StringEntity(vehicle.toString());
						res_string = entity.toString();
						Log.i("POST", res_string);
						http_request.setEntity(entity);

						// Send request to WCF service
						DefaultHttpClient httpClient = new DefaultHttpClient();
						HttpResponse response;
						try {
							response = httpClient.execute(http_request);
							resCode = response.getStatusLine().getStatusCode();
							res_string = response.toString();
							Log.i("TEST",
									"HTTP CODE: " + Integer.toString(resCode));

						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			// get the user_id and return back to MainActivity

			Intent i = getIntent();
			// i.putExtra("user_id", uid);
			setResult(Activity.RESULT_OK, i);
			finish();

		}
	}
}
