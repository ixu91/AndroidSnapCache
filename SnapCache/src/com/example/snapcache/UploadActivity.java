package com.example.snapcache;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class UploadActivity extends Activity {

	/** Called when the activity is first created. */

	private LocationManager locationManager;
	private static final String MY_API_KEY = "Alz5CvlnsTliNCseaYWgwz";

	String uid = "";
	String fb_token = "";
	String latitude = "";
	String longitude = "";
	String name = "";
	EditText et;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.upload_activity);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Bundle b = getIntent().getExtras();
		uid = b.getString("uid");
		
		et = (EditText) findViewById(R.id.editText1);

	}

	public void startPost(View view) {
		name = et.getText().toString();
		FilePickerAPI.setKey(MY_API_KEY);
		Uri uri = Uri
				.fromFile(new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/ttemp.txt"));
		Log.i("path", uri.getPath());
		Intent fpintent = new Intent(FilePicker.SAVE_CONTENT, uri, this,
				FilePicker.class);
		fpintent.putExtra("services", new String[] { FPService.DROPBOX,
				FPService.GALLERY, FPService.CAMERA, FPService.BOX,
				FPService.FLICKR, FPService.GDRIVE, FPService.GITHUB,
				FPService.GMAIL, FPService.INSTAGRAM, FPService.FACEBOOK });
		fpintent.putExtra("extension", ".jpg");
		startActivityForResult(fpintent, FilePickerAPI.REQUEST_CODE_SAVEFILE);

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Uri uri = data.getData();
		String url = data.getExtras().getString("fpurl");

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		String latitude = Double.toString(location.getLatitude());
		Log.i("FP-lat", latitude);

		String longitude = Double.toString(location.getLongitude());
		Log.i("FP-long", longitude);

		Intent intent = new Intent(this, PostActivity.class);

		Bundle b = new Bundle();
		b.putString("latitude", latitude);
		b.putString("longitude", longitude);
		b.putString("name", name);
		b.putString("uid", uid);
		Log.i("data",uid);

		b.putString("url", url);
		b.putString("type", "artifact");
		b.putString("file_type", "need file_type");
		//TODO: allow users to choose privacy setting
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
