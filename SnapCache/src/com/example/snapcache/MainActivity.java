package com.example.snapcache;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class MainActivity extends Activity {
	
    private Button logInButton;
    private TextView registerScreen;
    private TextView userText;
    private TextView passwordText;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private CheckBox saveLoginCheckBox;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
  	  Log.i("LOG", "app started");

//		LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
//		authButton.setReadPermissions(Arrays
//				.asList("user_likes", "user_status"));
        Session.openActiveSession(this, true, new Session.StatusCallback() {
        	
        	// callback when session changes state
        	
            @Override
            public void call(Session session, SessionState state, Exception exception) {
          	  Log.i("LOG", "try to find session");

              if (session.isOpened()) {

                // make request to the /me API
            	  Log.i("LOG", "callback worked");
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                  // callback after Graph API response with user object
                  @Override
                  public void onCompleted(GraphUser user, Response response) {
                	  Log.i("LOG", "onCompleted");

                    if (user != null) {
                       Intent i = new Intent(getApplicationContext(), ProfileActivity.class);     

                          Bundle firstName = new Bundle();
                          firstName.putString("userFirstName", user.getFirstName()); //Your id
                          Bundle lastName = new Bundle();
                          lastName.putString("userLastName", user.getLastName());
                          Bundle userId = new Bundle();
                          userId.putString("userId", user.getId());
                          i.putExtras(firstName); //Put your id to your next Intent
                          i.putExtras(lastName); 
                          i.putExtras(userId); 
                          GraphLocation gl= user.getLocation();
                          String country= "Israel";
                          String city ="Israel";
                          String street="Israel";
                          Bundle Country = new Bundle();
                          Country.putString("Country", country);
                          Bundle City = new Bundle();
                          City.putString("City", city);
                          Bundle Street = new Bundle();
                          Street.putString("Street", street);
                          i.putExtras(Country);
                          i.putExtras(City);
                          i.putExtras(Street);
                          startActivity(i);
                    }
                  }
                });
              }
              else {
            	  Log.i("LOG", "no session opened");
              }
            }
          });

          // Finish facebok login --------------------------------------------
          View titleView = getWindow().findViewById(android.R.id.title);
          if (titleView != null) {
              ViewParent parent = titleView.getParent();
              if (parent != null && (parent instanceof View)) {
               View parentView = (View)parent;
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
	


}
