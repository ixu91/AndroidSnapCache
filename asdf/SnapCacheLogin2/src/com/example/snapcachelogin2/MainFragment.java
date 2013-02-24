package com.example.snapcachelogin2;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONStringer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {

	// User Data Section
	private TextView userInfoTextView;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);

		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays
				.asList("user_likes", "user_status"));

		userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);

		return view;
	}

	private static final String TAG = "MainFragment";

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");

			userInfoTextView.setVisibility(View.VISIBLE);

			// Request user data and show the results
			Request.executeMeRequestAsync(session,
					new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								// Display the parsed user info
								userInfoTextView
										.setText(buildUserInfoDisplay(user));
								PostNewUser(user.getId(),user.getName());
							}
						}
					});
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			userInfoTextView.setVisibility(View.INVISIBLE);
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private UiLifecycleHelper uiHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	// Build the User Info; just a proof of concept
	private String buildUserInfoDisplay(GraphUser user) {
		StringBuilder userInfo = new StringBuilder("");

		// Example: typed access (name)
		// - no special permissions required
		userInfo.append(String.format("Name: %s\n\n", user.getName()));

		// Example: typed access (birthday)
		// - requires user_birthday permission
		userInfo.append(String.format("Birthday: %s\n\n", user.getBirthday()));

		// Example: partially typed access, to location field,
		// name key (location)
		// - requires user_location permission
		userInfo.append(String.format("Location: %s\n\n", user.getLocation()
				.getProperty("name")));

		// Example: access via property name (locale)
		// - no special permissions required
		userInfo.append(String.format("Locale: %s\n\n",
				user.getProperty("locale")));

		// Example: access via key for array (languages)
		// - requires user_likes permission
		// JSONArray languages = (JSONArray)user.getProperty("languages");
		// if (languages.length() > 0) {
		// ArrayList<String> languageNames = new ArrayList<String> ();
		// for (int i=0; i < languages.length(); i++) {
		// JSONObject language = languages.optJSONObject(i);
		// Add the language name to a list. Use JSON
		// methods to get access to the name field.
		// languageNames.add(language.optString("name"));
		// }
		// userInfo.append(String.format("Languages: %s\n\n",
		// languageNames.toString()));
		// }

		return userInfo.toString();
	}

	private String PostNewUser(String user_id, String name) {

		int resCode;
		String res_string = "no response set";

		// POST request to <service>/SaveVehicle
		HttpPost http_request = new HttpPost(
				"http://sheltered-falls-8280.herokuapp.com/users.json");
		// request.setHeader("Accept", "application/json");

		http_request.setHeader("Content-type", "application/json");
		http_request.setHeader("user-agent", "Yoda");

		// Build JSON string
		JSONStringer vehicle;
		try {
			vehicle = new JSONStringer().object().key("facebook_id").value(user_id)
					.key("name").value(name).endObject().endObject();

			StringEntity entity;
			try {
				entity = new StringEntity(vehicle.toString());

				http_request.setEntity(entity);

				// Send request to WCF service
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpResponse response;
				try {
					response = httpClient.execute(http_request);
					resCode = response.getStatusLine().getStatusCode();
					res_string = response.toString();
					return res_string;

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
		return res_string;

	}

	private String PostNewArtifact(String name, String pos, String uid,
			String file_url) {

		int resCode;
		String res_string = "no response set";

		// POST request to <service>/SaveVehicle
		HttpPost http_request = new HttpPost(
				"http://origin.staging.scion.com/PE/service/rest/getit");
		// request.setHeader("Accept", "application/json");

		http_request.setHeader("Content-type", "application/json");
		http_request.setHeader("user-agent", "Yoda");

		// Build JSON string
		JSONStringer vehicle;
		try {
			vehicle = new JSONStringer().object().key("getItInputTO").object()
					.key("zipCode").value("90505").key("financingOption")
					.value("B").key("make").value("Scion")
					.key("baseAmountFinanced").value("12000").key("modelYear")
					.value("2010").key("trimCode").value("6221")
					.key("totalMSRP").value("15000").key("aprRate").value("")
					.endObject().endObject();

			StringEntity entity;
			try {
				entity = new StringEntity(vehicle.toString());

				http_request.setEntity(entity);

				// Send request to WCF service
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpResponse response;
				try {
					response = httpClient.execute(http_request);
					resCode = response.getStatusLine().getStatusCode();
					res_string = response.toString();
					return Integer.toString(resCode);

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
		return res_string;

	}
	
	
}
