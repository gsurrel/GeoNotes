package org.surrel.geoposts;

import android.app.Activity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetLocation extends Activity {

	private GoogleMap map;

	@Override
	public void onCreate(Bundle icicle)
	{ 
		super.onCreate(icicle);
		setContentView(R.layout.activity_get_location);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

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
		try{
			Location location = locationManager.getLastKnownLocation(bestprovider);
			Log.v("Location: ", location.toString());

			LatLng MYPOSITION = new LatLng(location.getLatitude(),location.getLongitude());
			Marker mycurrentposition = map.addMarker(new MarkerOptions()
				.position(MYPOSITION)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
				.title("I am here")
				.anchor(0.5f,0.5f)
				.alpha(0.5f)
				.snippet("I love this place"));

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(MYPOSITION, 15));

			// Zoom in, animating the camera.
			//map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			//updateWithNewLocation(location);
		}catch(Exception e) {
			e.printStackTrace();
			Log.d("Refresh", "Error in block connection");
			Toast toast = Toast.makeText(getApplicationContext(), "Error loading map", Toast.LENGTH_LONG);
			toast.show();
		}
	}

	/*private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider){
			updateWithNewLocation(null);
		}
		public void onProviderEnabled(String provider){}
		public void onStatusChanged(String provider, int status, Bundle extras){}

	};

	private void updateWithNewLocation (Location location){
		String latLongString;
		TextView myLocationText;
		myLocationText = (TextView)findViewById(R.id.myLocationText);

		if (location != null) {
			Double geoLat = location.getLatitude()*1E6;
			Double geoLng = location.getLongitude()*1E6;


			double lat = location.getLatitude();
			double lng = location.getLongitude();

			latLongString = "Latitude:" + lat + "\nLongitude:" + lng;
		}
		else {
			latLongString = "No Location Found";
		}

		myLocationText.setText("Your current location is : \n" + latLongString);
	} */

}

