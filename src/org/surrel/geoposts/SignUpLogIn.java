package org.surrel.geoposts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SignUpLogIn extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_login);
}

	@Override
	public void onBackPressed() {
		// Otherwise defer to system default behavior.
		return;
	}

	public void GoToMain(View view)
	{
		Intent tomain = new Intent(this, MainActivity.class);
		startActivity(tomain);
	}

}
