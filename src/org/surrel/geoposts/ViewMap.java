package org.surrel.geoposts;

import java.util.Date;
import java.util.List;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewMap extends Activity implements OnMarkerClickListener {

	private GoogleMap map;

	@Override
	public void onCreate(Bundle icicle)
	{ 
		super.onCreate(icicle);
		setContentView(R.layout.activity_get_location);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Boolean engl = settings.getBoolean("English", true);
		Boolean ital = settings.getBoolean("Italian", true);
		Boolean germ = settings.getBoolean("German", true);
		Boolean fren = settings.getBoolean("French", true);
		Boolean comm = settings.getBoolean("Comment", true);
		Boolean reco = settings.getBoolean("Recommendation", true);
		Boolean sp_e = settings.getBoolean("Special_event", true);
		String per = settings.getString("geoperimeter", "10km");
		
		Double perimeter = (double)( Integer.parseInt( (per).replaceAll("[^0-9.]", "") ) );
		Integer en = bool_to_int(engl);
	    Integer fr = bool_to_int(fren);
	    Integer it = bool_to_int(ital);
	    Integer ge = bool_to_int(germ);
	    Integer com = bool_to_int(comm);
	    Integer recom = bool_to_int(reco);
	    Integer spec  = bool_to_int(sp_e);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		map.setMyLocationEnabled(true);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		List<String> providers = lm.getProviders(true);
		// Loop over array and if we get an accurate location we break out the loop
		Location my_latlon = null;
		for(int i=providers.size()-1; i>=0; i--)
		{
			my_latlon = lm.getLastKnownLocation(providers.get(i));
			if (my_latlon != null) break;
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
				String.valueOf(perimeter),
				String.valueOf(my_latlon.getLatitude()),
				String.valueOf(my_latlon.getLatitude()),
				String.valueOf(my_latlon.getLongitude()),
				String.valueOf(my_latlon.getLongitude()),
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
							intent.setClass(ViewMap.this, ViewGeopost.class);

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

