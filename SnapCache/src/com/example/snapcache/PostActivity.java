package com.example.snapcache;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class PostActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO Auto-generated method stub
	}

	@Override
	public void onStart() {
		super.onStart();

		Bundle b = getIntent().getExtras();
		String type = b.getString("type");
		String name = b.getString("name");
		String fb_id = b.getString("fb_id");

		Log.i("POST", "we are in the post activity");

		if (type.equals("artifact")) {
			HashMap<String, String> data = new HashMap<String, String>();
			// data.put("data_type", "picture");
			data.put("file_url",
					"https://www.filepicker.io/api/file/heHHkRRAaYp0e6qmruQm");
			// data.put("latitude", "43.21");
			// data.put("longitude", "203.32");
			data.put("name", "testmine");
			// data.put("privacy", "public");
			data.put("user_id", "102");
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
				// set up post data
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				Iterator<String> it = mData.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					Log.i("KEY", key);
					Log.i("WORD", mData.get(key));
					nameValuePair.add(new BasicNameValuePair(key, mData
							.get(key)));
				}

				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				HttpResponse response = client.execute(post);
				Log.i("POST", post.getRequestLine().toString());

				StatusLine statusLine = response.getStatusLine();
				Log.i("RES", Integer.toString(statusLine.getStatusCode()));

				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
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
//			i.putExtra("user_id", uid);
			setResult(Activity.RESULT_OK, i);
			finish();
			
		}
	}
}
