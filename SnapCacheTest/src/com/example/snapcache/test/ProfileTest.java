package com.example.snapcache.test;

import android.test.ActivityInstrumentationTestCase2;

import com.example.snapcache.MainActivity;
import com.example.snapcache.ProfileActivity;
import com.example.snapcache.UploadActivity;
import com.jayway.android.robotium.solo.Solo;

public class ProfileTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	public ProfileTest() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testActivityTestCaseSetUpProperly() {
        assertNotNull("activity should be launched successfully", getActivity());
    }
	
	public void testLogin() {
		solo.clickOnButton("Home");
		solo.assertCurrentActivity("Next Activity", ProfileActivity.class);
	}
	
	public void testViewPicture() {
		solo.clickOnButton("Home");
		solo.assertCurrentActivity("Next Activity", ProfileActivity.class);
		solo.clickInList(0);
		solo.assertCurrentActivity("Gallery", ProfileActivity.class);
	}
	
//	public void testUploadFile() {
//		solo.clickOnButton("Home");
//		solo.assertCurrentActivity("Next Activity", ProfileActivity.class);
//		solo.clickOnButton("Upload");
//		solo.assertCurrentActivity("Next Activity", UploadActivity.class);
//		solo.enterText(0, "UnitTest");
//		
//		solo.clickOnButton("Choose file to Upload");
////		solo.clickInList(0);
////		solo.goBack();
//		
//		solo.assertCurrentActivity("Next Activity", ProfileActivity.class);
//
//	}
	
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
	

}
