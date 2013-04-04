package com.example.snapcache;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.Session;

public class ProfileActivity extends Activity {

	private LocationManager locationManager;
	private Profile profile;

	String uid = "";
	String fb_token = "";
	private ListView lv;
	HashMap<String, String> urlToName;
	String file_loc = "";
	ImageView imgView;
	
	private File folder;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		profile = new Profile();
		fb_token = Session.getActiveSession().getAccessToken();
		setContentView(R.layout.profile_activity);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Bundle b = getIntent().getExtras();
		Log.i("PROFILE", "here");
		uid = b.getString("uid");
		
		lv = (ListView) findViewById(R.id.listView);
	}

	public void startPost(View view) {
		Intent in = new Intent(this, UploadActivity.class);
		Log.i("APP", "clicked");
		in.putExtra("uid", uid);
		startActivity(in);
	}

	public void showMapView(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		Log.i("Intent", "Going to MapView");
		startActivity(intent);
	}
	@Override
	protected void onStart() {
		super.onStart();
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		String latitude = Double.toString(location.getLatitude());
		String longitude = Double.toString(location.getLongitude());
		try {
			String requestUrl = profile.getRequestUrl(latitude, longitude, fb_token);
			new RequestTask().execute(requestUrl);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(folder.isDirectory()){
			String [] children = folder.list();
			for (String child : children)
				new File (folder, child).delete();
		}
	}

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			return profile.doInRequestBackground(uri);
		}

		@Override
		protected void onPostExecute(String result) {
			
			urlToName = new HashMap<String, String>();

			super.onPostExecute(result);
			urlToName = profile.parseResult(result);
			
			SimpleAdapter simpleAdpt = new SimpleAdapter(
					getApplicationContext(), profile.files,
					android.R.layout.simple_list_item_1,
					new String[] { "name" }, new int[] { android.R.id.text1 });

			lv.setAdapter(simpleAdpt);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> parentAdapter,
						View view, int position, long id) {

					// We know the View is a TextView so we can cast it
					TextView clickedView = (TextView) view;
					DownloadFile df = new DownloadFile();
					df.execute(urlToName.get(clickedView.getText()));
					Log.i("url2name", urlToName.get(clickedView.getText()));
				}
			});
		}
	}

	private class DownloadFile extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			Log.i("DFFile", "test");
			String fileLocation = profile.getFileLOC(sUrl);
			File videoFile2Play = new File(fileLocation);

			Intent i = new Intent();
			i.setAction(android.content.Intent.ACTION_VIEW);

			i.setDataAndType(Uri.fromFile(videoFile2Play), "image/*");
			startActivity(i);

			return null;
		}

	}
}