package org.surrel.geoposts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FirstLaunchActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_launch);
	}
	
	public void SignUpLogIn(View view)
	{
		Intent signuplogin = new Intent(this, LogIn.class);
		startActivity(signuplogin);
	}
	
	
}
