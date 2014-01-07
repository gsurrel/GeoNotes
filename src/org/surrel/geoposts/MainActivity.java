package org.surrel.geoposts;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
			return true;
		case R.id.preferences:
			startActivity(new Intent(this, Preferences.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
