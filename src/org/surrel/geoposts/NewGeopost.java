package org.surrel.geoposts;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.android.gms.maps.model.LatLng;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class NewGeopost extends Activity {

	private NumberPicker mp;
	private NumberPicker hp;
	private NumberPicker dp;
	private NumberPicker wp;

	private Button Cancel;
	private Button Submit;

	LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_geopost);

		mp = (NumberPicker) findViewById(R.id.month_picker);
		mp.setMaxValue(11);
		mp.setMinValue(0);

		wp = (NumberPicker) findViewById(R.id.week_picker);
		wp.setMaxValue(4);
		wp.setMinValue(0);

		dp = (NumberPicker) findViewById(R.id.day_picker);
		dp.setMaxValue(6);
		dp.setMinValue(0);

		hp = (NumberPicker) findViewById(R.id.hour_picker);
		hp.setMaxValue(23);
		hp.setMinValue(0);

		Cancel = (Button) findViewById(R.id.cancel_button);
		Cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancel_dialog = new AlertDialog.Builder(
						NewGeopost.this);
				cancel_dialog
				.setMessage(R.string.message_cancel)
				.setTitle(R.string.title_cancel)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						sendThenBackToMain();
					}
				})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();
					}
				});

				cancel_dialog.create().show();
			}
		});

		Submit = (Button) findViewById(R.id.submit_button);
		Submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancel_dialog = new AlertDialog.Builder(
						NewGeopost.this);
				cancel_dialog
				.setMessage(R.string.message_submit)
				.setTitle(R.string.title_submit)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						sendThenBackToMain();
					}
				})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();
					}
				});

				cancel_dialog.create().show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_geopost, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_send:
			Submit.performClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void sendThenBackToMain() {
		Log.d("Act.post", "Calling post");

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		List<String> providers = lm.getProviders(true);

		// Loop over array and if we get an accurate location we break out the loop
		Location my_latlon = null;
		for(int i=providers.size()-1; i>=0; i--)
		{
			my_latlon = lm.getLastKnownLocation(providers.get(i));
			if (my_latlon != null) break;
		}

		RequestTask rq = new RequestTask(getApplicationContext());
		String lang;
		int id_lang = ((Spinner) findViewById(R.id.chose_new_geopost_lang))
				.getSelectedItemPosition();
		switch (id_lang) {
		case 1:
			lang = "fr";
			break;
		case 2:
			lang = "it";
			break;
		case 3:
			lang = "ge";
			break;
		case 0:
		default:
			lang = "en";
		}
		rq.execute(
				"post",
				lang,
				String.valueOf(((Spinner)findViewById(R.id.chose_new_geopost_cat)).getSelectedItemId()),
				String.valueOf(((NumberPicker) findViewById(R.id.month_picker)).getValue()*3600*24*30
						+ ((NumberPicker) findViewById(R.id.week_picker)).getValue()*3600*24*7
						+ ((NumberPicker) findViewById(R.id.day_picker)).getValue()*3600*24
						+ ((NumberPicker) findViewById(R.id.hour_picker)).getValue()*3600),
						((EditText) findViewById(R.id.chose_new_geopost_title)).getText().toString(),
						((EditText) findViewById(R.id.chose_new_geopost_text)).getText().toString(),
						String.valueOf(my_latlon.getLatitude()),
						String.valueOf(my_latlon.getLongitude()));
		Log.d("Act.post", "Called post");

		String result;
		Resources res = getResources();
		try {
			result = rq.get(5, TimeUnit.SECONDS);
			Log.d("Act.post", result);

			if (result == res.getString(R.string.post_ok)) {
				Intent back_to_main = new Intent(this, MainActivity.class);
				startActivity(back_to_main);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
