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
import android.view.View;
import android.widget.Toast;

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
		Log.i("PROFILE", "here");
		uid = b.getString("uid");
		
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
		Log.i("FP", "this is the one");
		String url = data.getExtras().getString("fpurl");
		Log.i("FP", url);

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		String latitude = Double.toString(location.getLatitude());
		String longitude = Double.toString(location.getLongitude());

		Intent intent = new Intent(this, PostActivity.class);

		Bundle b = new Bundle();
		b.putString("latitude", latitude);
		b.putString("longitude", longitude);
		b.putString("name", "android_file");
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

	@Override
	protected void onStart(){
		super.onStart();
		  Criteria criteria = new Criteria();
				String provider = locationManager.getBestProvider(criteria, false);
				Location location = locationManager.getLastKnownLocation(provider);
				String latitude = Double.toString(location.getLatitude());
				String longitude = Double.toString(location.getLongitude());
				try {
					getJSON(latitude, longitude);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
				//list stuff
				
		
	}

	@Override
	protected void onPause(){
		super.onPause();
		
	}

	private String getJSON(String latitude, String longitude) throws ClientProtocolException, IOException{
		Log.i("Test","In getJSon");
		int resCode;
		String res_string = "";
		String url = "http://sheltered-falls-8280.herokuapp.com/artifacts/show_all_nearby.json?";
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		
		sb.append("latitude=");
		sb.append("37.785834");
		sb.append("&longitude=");
		sb.append("-122.40641");
		String finalUrl = sb.toString();
		new RequestTask().execute(finalUrl);
		return "";
	}
	
	class RequestTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(uri[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        Log.i("S", result);
	        String [] tokens = result.split(",");
	        String url = (tokens[2].split(":\""))[1].split("\"")[0]; 
	        Log.i("S", url);
	        Toast toast = Toast.makeText(getApplicationContext(), "hi" + result, 50);
	        toast.show();
	    }
	}

}
