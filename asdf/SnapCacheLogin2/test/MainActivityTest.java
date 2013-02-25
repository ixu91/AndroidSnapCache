package com.example.snapcachelogin2;

import static org.junit.Assert.assertEquals;
import junit.framework.TestCase;

public class MainActivityTest extends TestCase {

	public void testPostArtifact() {
		MainActivity main = new MainActivity();
		String ret = main.PostNewArtifact("test", "51.30", "352.438", "102",
				"http://www.google.com", "picture", "public");
		assertEquals(ret, "Artifact posted");
	}
	
	public void testPostArtifactBad() {
		MainActivity main = new MainActivity();
		String ret = main.PostNewArtifact("test", "51.30", "352.438", "102",
				"www.google.com", "picture", "public");
		assertEquals(ret, "422");
	}
	
	public void testPostUser() {
		MainFragment main = new MainFragment();
		String ret = main.PostNewUser("13242234234", "test");
		assertEquals(ret, "201");
	}
	
	public void testPostUserBad() {
		MainFragment main = new MainFragment();
		String ret = main.PostNewUser("", "test");
		assertEquals(ret, "422");
	}
	
	public void testGetUser() {
		MainFragment main = new MainFragment();
		int ret = main.GetUser("TestFacebook1", "test");
		assertEquals(ret, 1);
	}

	public void testGetUserBad() {
		MainFragment main = new MainFragment();
		int ret = main.GetUser("TestFacebookasd1", "test");
		assertEquals(ret, -1);
	}
}
