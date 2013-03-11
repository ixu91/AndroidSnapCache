package com.example.snapcache;

import io.filepicker.FPService;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;

public class ProfileActivity extends Activity {

	private static final String MY_API_KEY = "Alz5CvlnsTliNCseaYWgwz";
	
	private LocationManager locationManager;
	
	//private HashMap<String, String> files;
	
	private HashMap<String, String> urlToName;
	String uid = "";
	String fb_token = "";
	private ListView lv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fb_token=Session.getActiveSession().getAccessToken();
		setContentView(R.layout.profile_activity);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Bundle b = getIntent().getExtras();
		urlToName = new HashMap<String, String>();
		Log.i("PROFILE", "here");
		uid = b.getString("uid");
		lv = (ListView) findViewById(R.id.listView);		

		//		Intent intent = new Intent(this, ProxAlertService.class);
		//		startService(intent);

		//		Intent intent = new Intent(this, ProxAlertActivity.class);
		//		startActivity(intent);
		// uid = b.getString("uid");

		// TODO Auto-generated method stub
	}

	public void startPost(View view) {

		FilePickerAPI.setKey(MY_API_KEY);
		Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp")); 
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
		
		Uri uri = data.getData();
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
		b.putString("name", "testPickup");
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
		sb.append(latitude);
		sb.append("&longitude=");
		sb.append(longitude);
		sb.append("&fb_token=");
		sb.append(fb_token);
		String finalUrl = sb.toString();
		Log.i("Test","get gets called");
		new RequestTask().execute(finalUrl);
		return finalUrl;
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
			List<Map<String,String>> files = new ArrayList<Map<String,String>>();
			super.onPostExecute(result);
			Log.i("S", result);
			String [] entries = result.split("\\},");
			for(String entry: entries) {
				Log.i("S", Integer.toString(entries.length));
				String [] tokens = entry.split(",");
				String url = (tokens[2].split(":\""))[1].split("\"")[0]; 
				String name = (tokens[6].split(":\""))[1].split("\"")[0];
				Log.i("S", url);
				Log.i("S", name);
				//Toast toast = Toast.makeText(getApplicationContext(), "hi" + result, 50);
				//toast.show();
				//files.put(name, url);
				Log.i("ADD A FILE NOW", "ADD FILE");
				files.add(getFileURL("name",name));
				Log.i("ADDS URL TO FILES HASH", "ADDDDS");
				urlToName.put(name, url);
			}
			
			SimpleAdapter simpleAdpt = new SimpleAdapter(getApplicationContext(), files, android.R.layout.simple_list_item_1,new String[] {"name"}, new int[]{android.R.id.text1});

			Log.i("ADAPTER", "adapter after");
			lv.setAdapter(simpleAdpt);
			Log.i("ADAPTER", "adapt lv aft");
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				 
			     public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
			                             long id) {
			              
			      
			         // We know the View is a TextView so we can cast it
			         TextView clickedView = (TextView) view;
			         DownloadFile df = new DownloadFile();
			         df.execute(urlToName.get(clickedView.getText()));
			         File videoFile2Play = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.png");
			         Intent i = new Intent();
			         i.setAction(android.content.Intent.ACTION_VIEW);
			         
			         i.setDataAndType(Uri.fromFile(videoFile2Play), "image/*");
			         startActivity(i);
			         
			 
			     }
			});

		}
	}
	
	private HashMap<String, String> getFileURL(String key, String value){
		HashMap<String, String> fileURL = new HashMap<String,String>();
		Log.i("TEST", value);
		
		fileURL.put(key, value);
		return fileURL;
	}

	private class DownloadFile extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			try {
				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				// this will be useful so that you can show a typical 0-100% progress bar
				int fileLength = connection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/test.png");
				Log.i("S", "here?");
				byte data[] = new byte[1024];
				
				int count;
				while ((count = input.read(data)) != -1) {
					
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				Log.d("Exception", e.getMessage());
			}
			return null;
		}

	}
}