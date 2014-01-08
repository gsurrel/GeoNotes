package org.surrel.geoposts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_logged_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.log_out:
			startActivity(new Intent(this, FirstLaunchActivity.class));
			return true;
		case R.id.preferences:
			Log.d("Menu", "Calling prefs");
			startActivity(new Intent(this, Preferences.class));
			return true;
		case R.id.refresh:
			Log.d("Menu", "Calling refresh");
			new RequestTask(getApplicationContext()).execute("http://gregoire.surrel.org/gps/?api");
			Log.d("Menu", "Called refresh");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Test if Google Play Services installed
		int gps = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		CharSequence text = "";
		int duration = Toast.LENGTH_LONG;
		switch(gps){
		case ConnectionResult.SUCCESS:
			text = "Google Play Services loaded";
			duration = Toast.LENGTH_SHORT;
			break;
		case ConnectionResult.SERVICE_MISSING:
			text = "Google Play Services missing";
			break;
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			text = "Google Play Services needs an update";
			break;
		case ConnectionResult.SERVICE_DISABLED:
			text = "Google Play Services disabled";
			break;
		case ConnectionResult.SERVICE_INVALID:
			text = "Google Play Services invalid";
			break;
		case ConnectionResult.DATE_INVALID:
			text = "Google Play Services date invalid";
			break;
		default:
			text = "Unknown result";
			break;
		}
		Toast toast = Toast.makeText(getApplicationContext(), text, duration);
		toast.show();
	}

	public void ViewMap(View view) {
		Intent viewmap = new Intent(this, GetLocation.class);
		startActivity(viewmap);
	}

	public void NewPost(View view) {
		Intent newpost = new Intent(this, NewGeopost.class);
		startActivity(newpost);
	}

	public void SetPreferences(View view) {
		Intent preferences = new Intent(this, Preferences.class);
		startActivity(preferences);
	}

	public void ViewPosts(View view) {
		Intent viewposts = new Intent(this, MyGeoposts.class);
		startActivity(viewposts);
	}

}