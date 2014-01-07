package org.surrel.geoposts;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class NewGeopost extends Activity {
	
	private NumberPicker mp;
	private NumberPicker hp;
	private NumberPicker dp;
	private NumberPicker wp;
	
	private Button Cancel;
	
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
				AlertDialog.Builder cancel_dialog = new AlertDialog.Builder(NewGeopost.this);
				cancel_dialog.setMessage(R.string.message_cancel)
				       .setTitle(R.string.title_cancel)
				       .setCancelable(false)
				       .setPositiveButton(R.string.yes,
				    		   new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									BackToMain();
								}
							})
					   .setNegativeButton(R.string.no,
							   new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
				
				cancel_dialog.create().show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_logged_in, menu);
		return true;
	}
	
	public void BackToMain()
	{
		Intent back_to_main  = new Intent(this, MainActivity.class);
		startActivity(back_to_main);
	}

}
