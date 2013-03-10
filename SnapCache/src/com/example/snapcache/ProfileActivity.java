package com.example.snapcache;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ProfileActivity extends Activity {

	private static final String MY_API_KEY = "Alz5CvlnsTliNCseaYWgwz";

	private LocationManager locationManager;
	String uid = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Bundle b = getIntent().getExtras();
		
//		Intent intent = new Intent(this, ProxAlertService.class);
//		startService(intent);
		
//		Intent intent = new Intent(this, ProxAlertActivity.class);
//		startActivity(intent);
		// uid = b.getString("uid");

		// TODO Auto-generated method stub
	}

	public void startPost(View view) {

		FilePickerAPI.setKey(MY_API_KEY);
		Uri uri = Uri.fromFile(new File("/tmp/android.txt")); // a uri
																// to
																// the
																// content
																// to
																// save
		Intent fpintent = new Intent(FilePicker.SAVE_CONTENT, uri, this,
				FilePicker.class);
		fpintent.putExtra("services", new String[] { FPService.DROPBOX,
				FPService.GALLERY, FPService.CAMERA, FPService.BOX,
				FPService.FLICKR, FPService.GDRIVE, FPService.GITHUB,
				FPService.GMAIL, FPService.INSTAGRAM, FPService.FACEBOOK });
		startActivityForResult(fpintent, FilePickerAPI.REQUEST_CODE_SAVEFILE);

		// fileUrl = intent.getExtras().getString("fpurl");
		// Log.i("file", fileUrl);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("LOG", "Does the code get here?");
		Log.i("LOG", "this one?");
		Uri uri = data.getData();

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		String latitude = Double.toString(location.getLatitude());
		String longitude = Double.toString(location.getLongitude());

		Intent intent = new Intent(this, PostActivity.class);

		Bundle b = new Bundle();
		b.putString("latitude", latitude);
		b.putString("longitude", longitude);
		String name = "newtest";
		b.putString("name", name);
		b.putString("uid", uid);
		String url = "http://www.google.com";
		b.putString("url", url);
		String type = "artifact";
		b.putString("type", type);
		b.putString("file_type", "need file_type");
		String privacy = "public";
		b.putString("privacy", privacy);
		intent.putExtras(b);

		startActivity(intent);

		if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
			if (resultCode != RESULT_OK)
				// Result was cancelled by the user or there was an error
				return;
			Uri uri1 = data.getData();
			Log.i("file", "FPUrl: " + data.getExtras().getString("fpurl"));
			return;
		}
	}

}
