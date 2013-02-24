package com.example.snapcachelogin2;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONStringer;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity {

	private MainFragment mainFragment;

	private static final String MY_API_KEY = "Alz5CvlnsTliNCseaYWgwz";

	private String fileUrl = "";

	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mainFragment).commit();
		} else {
			// Or set the fragment from restored state info
			mainFragment = (MainFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void startUpload(View view) {
		FilePickerAPI.setKey(MY_API_KEY);
		Uri uri = Uri.fromFile(new File("mnt/sdcard/Movies/test")); // a uri to
																	// the
																	// content
																	// to save
		Intent intent = new Intent(FilePicker.SAVE_CONTENT, uri, this,
				FilePicker.class);
		intent.putExtra("services", new String[] { FPService.DROPBOX,
				FPService.GALLERY, FPService.CAMERA, FPService.BOX,
				FPService.FLICKR, FPService.GDRIVE, FPService.GITHUB,
				FPService.GMAIL, FPService.INSTAGRAM, FPService.FACEBOOK });
		startActivityForResult(intent, FilePickerAPI.REQUEST_CODE_SAVEFILE);
		// fileUrl = intent.getExtras().getString("fpurl");
		// Log.i("file", fileUrl);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("LOG", "Does the code get here?");
		Log.i("LOG", "this one?");
		Uri uri = data.getData();
		
		String fileUrl = data.getExtras().getString("fpurl");
		Bundle bundle = data.getExtras();
		for (String key : bundle.keySet())
			Log.v("key", uri.getPath());
		Log.i("LOG", "dsafsadf");
		if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
			if (resultCode != RESULT_OK)
				// Result was cancelled by the user or there was an error
				return;
//			Uri uri = data.getData();
//			Log.i("file", "FPUrl: " + data.getExtras().getString("fpurl"));
		}
	}

	public void startPost(View view) {

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		String latitude = Double.toString(location.getLatitude());
		String longitude = Double.toString(location.getLongitude());
		PostNewArtifact("test", latitude, longitude, "102",
				"http://www.google.com", "picture", "public");
		Log.i("lat", latitude);
		Log.i("long", longitude);

	}

	private String PostNewArtifact(String name, String lat, String lon,
			String uid, String file_url, String type, String privacy) {

		int resCode;
		String res_string = "";

		// POST request to <service>/SaveVehicle
		HttpPost http_request = new HttpPost(
				"http://sheltered-falls-8280.herokuapp.com/artifacts.json");

		http_request.setHeader("Content-type", "application/json");
		http_request.setHeader("user-agent", "Yoda");

		// Build JSON string
		JSONStringer vehicle;
		try {
			vehicle = new JSONStringer().object().key("data_type").value(type)
					.key("file_url").value(file_url).key("latitude").value(lat)
					.key("longitude").value(lon).key("name").value(name)
					.key("privacy").value(privacy).key("user_id").value(uid)
					.endObject();

			StringEntity entity;
			try {
				entity = new StringEntity(vehicle.toString());
				res_string = entity.toString();

				Log.i("JSON", vehicle.toString());
				http_request.setEntity(entity);

				// Send request to WCF service
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpResponse response;
				try {
					response = httpClient.execute(http_request);
					resCode = response.getStatusLine().getStatusCode();
					res_string = response.toString();
					res_string = Integer.toString(resCode);
					Log.i("CODE", res_string);

					if (resCode == 201) {
						res_string = "Artifact posted";
					}

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
