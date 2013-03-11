package com.example.snapcache;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewParent;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

	String name = "";
	String fb_id = "";
	String uid = "";
	String fb_token = "";

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

		// get the new user id after a post
		if (data.getExtras().containsKey("user_id")) {
			uid = data.getStringExtra("user_id");
		}

		Log.i("LOG", "getting active session");

		// sometimes its just jumps to hear, so alway get to the Profile page
		if (Session.getActiveSession().isOpened()) {
			Intent i = new Intent(getApplicationContext(),
					ProfileActivity.class);
			Bundle b = new Bundle();
			b.putString("uid", uid);
			i.putExtra("uid", uid);
			startActivity(i);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("LOG", "app started");
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state

			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {
				Log.i("LOG", "try to find session");

				if (session.isOpened()) {

					// make request to the /me API
					Log.i("LOG", "callback worked");
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {

								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									Log.i("LOG", "onCompleted");

									if (user != null) {
										fb_token = session.getAccessToken();
										Log.i("FB", user.getId());
										fb_id = user.getId();
										name = user.getName();
										new RequestTask()
												.execute("https://sheltered-falls-8280.herokuapp.com/users/get_by_facebook_id.json?facebook_id="
														+ fb_id + "&fb_token=" + fb_token);

//										Intent i = new Intent(
//												getApplicationContext(),
//												ProfileActivity.class);
//										Bundle b = new Bundle();
//										b.putString("uid", uid);
//										startActivity(i);
									}
								}
							});
				} else {
					Log.i("LOG", "no session opened");
				}
			}
		});

		// Finish facebok login --------------------------------------------
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class RequestTask extends AsyncTask<String, String, String> {

		private static final int STATIC_INTEGER_VALUE = 0;

		@Override
		protected String doInBackground(String... uri) {
			Log.i("KET", "did i get here");
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				response = httpclient.execute(new HttpGet(uri[0]));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
					Log.i("HTTP", "theres no error");

				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				Log.i("HTTP", "theres an error");

				// TODO Handle problems..
			} catch (IOException e) {
				Log.i("HTTP", "theres an error");

				// TODO Handle problems..
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("RES", "onPostExecute");
			super.onPostExecute(result);
			if (result != null) {
				// Log.i("USER", result);
				String[] result_ar = result.split(",");
				// for (String str : result_ar) {
				// Log.i("RESULT", str);
				// }
				Log.i("RES", result_ar[2].substring(5));
				uid = result_ar[2].substring(5);

				Intent i = new Intent(getApplicationContext(),
						ProfileActivity.class);
				Bundle b = new Bundle();
				i.putExtra("uid", uid);
				b.putString("uid", uid);
				Log.i("UID", uid);
				startActivity(i);

			} else {
				// does not exist, so must post new user
				Log.i("RES", "new user");
				Intent intent = new Intent(MainActivity.this,
						PostActivity.class);
				Bundle b = new Bundle();
				b.putString("name", name);
				b.putString("type", "user");
				b.putString("fb_id", fb_id);
				intent.putExtras(b);
				startActivityForResult(intent, STATIC_INTEGER_VALUE);

			}
		}
	}
}
