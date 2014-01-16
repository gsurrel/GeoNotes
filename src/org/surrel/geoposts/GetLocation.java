package org.surrel.geoposts;

import java.util.Date;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetLocation extends Activity implements OnMarkerClickListener {

	private GoogleMap map;

	@Override
	public void onCreate(Bundle icicle)
	{ 
		super.onCreate(icicle);
		setContentView(R.layout.activity_get_location);

		Double perimeter = (double)( Integer.parseInt( (Preferences.perimeter).replaceAll("[^0-9.]", "") ) );
		Log.e("success"," value in map perimeter = " + String.valueOf(perimeter));
		Integer en = bool_to_int(Preferences.engl);
		Integer fr = bool_to_int(Preferences.fren);
		Integer it = bool_to_int(Preferences.ital);
		Integer ge = bool_to_int(Preferences.germ);
		Integer com = bool_to_int(Preferences.comm);
		Log.e("success","value in map com = " + String.valueOf(com));
		Integer recom = bool_to_int(Preferences.reco);
		Log.e("success","value in map recom = " + String.valueOf(recom));
		Integer spec  = bool_to_int(Preferences.sp_e);

		Log.e("success", "load finished");

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		map.setMyLocationEnabled(true);

		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(context);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String bestprovider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(bestprovider);

		LatLng my_position;

		try {
			my_position = new LatLng(location.getLatitude(),location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_position, 15));
		} catch (Exception e) {

			my_position = new LatLng(0,0);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_position, 1));
		}

		// Open read access for local database
		NotesDbHelper notesDbHelper = new NotesDbHelper(getApplicationContext());
		SQLiteDatabase db = notesDbHelper.getReadableDatabase();
		String[] columns = new String[]{
				"ID", "lat", "lon", "title",
				"text", "user", "karma", "creation",
				"lifetime", "lang", "cat", "creation + lifetime AS expiration"
		};

		int currentTime = (int) ((new Date().getTime())/1000);
		Log.i("Current time", String.valueOf(currentTime/1000));

		String[] selectionArgs = new String[]{
				String.valueOf(perimeter*10000),
				String.valueOf(my_position.latitude),
				String.valueOf(my_position.latitude),
				String.valueOf(my_position.longitude),
				String.valueOf(my_position.longitude),
				// TODO: get this filter working. Works fine out of SQL request
				//String.valueOf(currentTime),
				String.valueOf(recom),
				String.valueOf(0),
				String.valueOf(com),
				String.valueOf(1),
				String.valueOf(spec),
				String.valueOf(2),
				String.valueOf(en),
				"en",
				String.valueOf(it),
				"it",
				String.valueOf(ge),
				"ge",
				String.valueOf(fr),
				"fr"
		};

		Log.e("Success", "before filtering");
		Cursor result = db.query(
				"notes",
				columns,
				"( (?*360/40075) > ( (?-lat)*(?-lat) + (?-lon)*(?-lon) ) ) " +
						// TODO: get this filter working. Works fine out of SQL request
						//"AND ? < expiration " +
						"AND ((? AND (cat = ?)) OR (? AND (cat = ?)) OR (? AND (cat = ?)) ) " +		
						"AND ((? AND (lang = ?)) OR (? AND (lang = ?)) OR (? AND (lang = ?)) OR (? AND (lang = ?)))",
						selectionArgs,
						null, null, null); // Not grouping, filtering or sorting

		Log.e("Success","filtering done successfully !!");

		if(result.moveToFirst())
		{
			do
			{
				// TODO: get this filter working. Works fine out of SQL request
				if(currentTime < result.getInt(result.getColumnIndex("expiration")))
				{
					int id = result.getInt(result.getColumnIndex("ID"));
					LatLng latlng = new LatLng(
							result.getDouble(result.getColumnIndex("lat")),
							result.getDouble(result.getColumnIndex("lon"))
							);
					int cat = result.getInt(result.getColumnIndex("cat"));

					int resId;
					switch(cat)
					{
					case 0:
						resId = R.drawable.recommendation_icon;
						break;
					case 1:
						resId = R.drawable.comment_icon;
						break;
					case 2:
						resId = R.drawable.special_event_icon;
						break;
					default:
						resId = R.drawable.ic_launcher;
					}
					BitmapDescriptor geoicon = BitmapDescriptorFactory.fromResource(resId);

					map.addMarker(new MarkerOptions().position(latlng)
							.icon(geoicon)
							.anchor(0.5f,0.5f)
							.alpha(0.5f)
							.title(String.valueOf(id))
							);

					map.setOnMarkerClickListener(new OnMarkerClickListener()
					{
						@Override
						public boolean onMarkerClick(Marker marker) {
							Intent intent = new Intent();
							intent.setClass(GetLocation.this, ViewGeopost.class);

							intent.putExtra("id", marker.getTitle());

							startActivity(intent);
							return true;
						}
					});
				}
			} while(result.moveToNext());
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return true;
	}

	public Integer bool_to_int (Boolean bool) {
		if ( bool.equals(Boolean.TRUE) )
		{
			return 1;
		}
		else { 
			return 0;
		}	
	}
}

