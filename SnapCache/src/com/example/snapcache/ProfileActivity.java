package com.example.snapcache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
//		uid = b.getString("uid");

		// TODO Auto-generated method stub
	}

	public void startPost(View view) {
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		String latitude = Double.toString(location.getLatitude());
		String longitude = Double.toString(location.getLongitude());

		Intent intent = new Intent(this, PostActivity.class);

		Bundle b = new Bundle();
		b.putString("latitude", latitude);
		b.putString("longitude", longitude);
		String name = "need to add";
		b.putString("name", name);
		b.putString("uid", uid);
		String url = "need url";
		b.putString("url", url);
		String type = "need type";
		b.putString("type", type);
		String privacy = "public";
		b.putString("privacy", privacy);
		intent.putExtras(b);

		startActivity(intent);

	}

}
