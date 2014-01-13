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
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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

		LatLng my_positions;

		try {
			my_positions = new LatLng(location.getLatitude(),location.getLongitude());

			map.addMarker(new MarkerOptions()
			.position(my_positions)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
			.title("I am here")
			.anchor(0.5f,0.5f)
			.alpha(0.5f)
			.snippet("I love this place"));

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_positions, 15));
		} catch (Exception e) {
			// No position
			my_positions = new LatLng(0,0);

			map.addMarker(new MarkerOptions()
			.position(my_positions)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
			.title("Position not found")
			.anchor(0.5f,0.5f)
			.alpha(0.5f)
			.snippet("Try again later, maybe?"));

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_positions, 1));
		}

		// Open read access for local database
		NotesDbHelper notesDbHelper = new NotesDbHelper(getApplicationContext());
		SQLiteDatabase db = notesDbHelper.getReadableDatabase();
		String[] columns = new String[]{
				"ID", "lat", "lon", "title",
				"text", "user", "karma", "creation",
				"lifetime", "lang", "cat"
		};
		String limit = "0.1"; // TODO: 
		String[] selectionArgs = new String[]{
				String.valueOf(my_positions.latitude),
				limit,
				String.valueOf(my_positions.latitude),
				limit,
				String.valueOf(my_positions.longitude),
				limit,
				String.valueOf(my_positions.longitude),
				limit
		};
		Cursor result = db.query(
				"notes",
				columns,
				"lat > ? - ? AND lat < ? + ? AND lon < ? + ? AND lon > ? - ?",
				selectionArgs,
				null, null, null); // Not grouping, filtering or sorting

		if(result.moveToFirst())
		{
			do
			{
				int id = result.getInt(result.getColumnIndex("ID"));
				LatLng latlng = new LatLng(
						result.getDouble(result.getColumnIndex("lat")),
						result.getDouble(result.getColumnIndex("lon"))
						);
				String title = result.getString(result.getColumnIndex("title"));
				String text = result.getString(result.getColumnIndex("text"));
				int user = result.getInt(result.getColumnIndex("user"));
				int karma = result.getInt(result.getColumnIndex("karma"));
				Date creation = new Date(1000*result.getLong(result.getColumnIndex("creation")));
				int lifetime = result.getInt(result.getColumnIndex("lifetime"));
				String lang = result.getString(result.getColumnIndex("lang"));
				int cat = result.getInt(result.getColumnIndex("cat"));

				Log.d("Map", String.valueOf(id));
				Log.d("Map", latlng.toString());
				Log.d("Map", title);
				//Log.d("Map", text);
				Log.d("Map", String.valueOf(user));
				Log.d("Map", String.valueOf(karma));
				Log.d("Map", creation.toGMTString());
				Log.d("Map", String.valueOf(lifetime));
				Log.d("Map", lang);
				Log.d("Map", String.valueOf(cat));

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
						.title(title)
						.snippet(text)
						);

				//			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(id)
				//			{
				//				private int ID;
				//				
				//				public void OnInfoWindowClickListener(int id) {
				//					super.OnInfoWindowClickListener();
				//					ID = id;
				//				}
				//				
				//				@Override
				//				public void onInfoWindowClick(Marker marker) {
				//					Intent intent = new Intent();
				//					intent.setClass(GetLocation.this, ViewGeopost.class);
				//
				//					intent.putExtra("id", marker.getId());
				//					intent.putExtra("text", "geopost text blabla blabla bla");
				//					intent.putExtra("title", "geopost title");
				//					intent.putExtra("creation", "geopost creation");
				//					intent.putExtra("user", "geopost user");
				//
				//					startActivity(intent);
				//				}
				//			});

			} while(result.moveToNext());
		}



		//		LatLng GEOPOSITION = new LatLng(lat,lng);
		//
		//		BitmapDescriptor geoicon = null;
		//
		//		//String geocategory = null;
		//
		//		if ( ((Node)CatList.item(0)).getNodeValue().trim().equalsIgnoreCase("1" )) {
		//			geoicon = BitmapDescriptorFactory.fromResource(R.drawable.recommendation_icon);
		//			//geocategory = getResources().getString(R.string.recommendation);
		//		}
		//		if ( ((Node)CatList.item(0)).getNodeValue().trim().equalsIgnoreCase("2" )) {
		//			geoicon = BitmapDescriptorFactory.fromResource(R.drawable.comment_icon);
		//			//geocategory = getResources().getString(R.string.comment);
		//		}
		//		if ( ((Node)CatList.item(0)).getNodeValue().trim().equalsIgnoreCase("3" )) {
		//			geoicon = BitmapDescriptorFactory.fromResource(R.drawable.special_event_icon);
		//			//geocategory = getResources().getString(R.string.special_event);
		//		}
		//
		//		String geouser = ((Node)UsernameList.item(0)).getNodeValue().trim();
		//		String geotitle = ((Node)TitleList.item(0)).getNodeValue().trim();
		//		String geotext = ((Node)TextList.item(0)).getNodeValue().trim();
		//		String geocreation = ((Node)CreaList.item(0)).getNodeValue().trim();
		//
		//		map.addMarker (new MarkerOptions().position(GEOPOSITION)
		//				.icon(geoicon)
		//				.anchor(0.5f,0.5f)
		//				.alpha(0.5f)
		//				.title(geouser + geotitle + geocreation)
		//				.snippet(geotext)
		//				);
		//
		//		map.setOnMarkerClickListener(new OnMarkerClickListener()
		//		{
		//
		//			@Override
		//			public boolean onMarkerClick(Marker marker) {
		//				Intent intent = new Intent();
		//				intent.setClass(GetLocation.this, ViewGeopost.class);
		//
		//				intent.putExtra("category", getResources().getString(R.string.comment));
		//				intent.putExtra("text", "geopost text blabla blabla bla");
		//				intent.putExtra("title", "geopost title");
		//				intent.putExtra("creation", "geopost creation");
		//				intent.putExtra("user", "geopost user");
		//
		//				startActivity(intent);
		//				return true;
		//			}
		//
		//		});









		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################
		// ######################################################








		//		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		//		DocumentBuilder docBuilder = null;
		//		Document doc = null;
		//
		//		try {
		//			docBuilder = docBuilderFactory.newDocumentBuilder();
		//		} catch (ParserConfigurationException e) {
		//			Log.v("tag1","1");
		//			e.printStackTrace();
		//		}
		//
		//		try {
		//			doc = docBuilder.parse(getAssets().open("geoposts.xml"));
		//		} catch (SAXException e) {
		//			Log.v("tag2","1");
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			Log.v("tag2","2");
		//			e.printStackTrace();
		//		}
		//
		//		doc.getDocumentElement().normalize();
		//
		//		NodeList geopostsList = doc.getElementsByTagName("data");
		//
		//		for (int i = 0; i < geopostsList.getLength(); i++) {
		//
		//			Node firstgeopostNode = geopostsList.item(i);
		//
		//			if(firstgeopostNode.getNodeType() == Node.ELEMENT_NODE){
		//
		//				Element firstgeopostElement = (Element)firstgeopostNode;
		//
		//				NodeList catList = firstgeopostElement.getElementsByTagName("category");
		//				Element firstCatElement = (Element)catList.item(0);
		//				NodeList CatList = firstCatElement.getChildNodes();
		//
		//				NodeList titleList = firstgeopostElement.getElementsByTagName("title");
		//				Element firstTitleElement = (Element)titleList.item(0);
		//				NodeList TitleList = firstTitleElement.getChildNodes();
		//
		//				NodeList usernameList = firstgeopostElement.getElementsByTagName("username");
		//				Element firstUsernameElement = (Element)usernameList.item(0);
		//				NodeList UsernameList = firstUsernameElement.getChildNodes();
		//
		//				NodeList latList = firstgeopostElement.getElementsByTagName("latitude");
		//				Element firstLatElement = (Element)latList.item(0);
		//				NodeList LatList = firstLatElement.getChildNodes();
		//
		//				NodeList lngList = firstgeopostElement.getElementsByTagName("longitude");
		//				Element firstLngElement = (Element)lngList.item(0);
		//				NodeList LngList = firstLngElement.getChildNodes();
		//
		//				NodeList creaList = firstgeopostElement.getElementsByTagName("creation");
		//				Element firstCreaElement = (Element)creaList.item(0);
		//				NodeList CreaList = firstCreaElement.getChildNodes();
		//
		//				NodeList textList = firstgeopostElement.getElementsByTagName("text");
		//				Element firstTextElement = (Element)textList.item(0);
		//				NodeList TextList = firstTextElement.getChildNodes();
		//
		//				int lat = Integer.parseInt(((Node)LatList.item(0)).getNodeValue().trim());
		//				int lng = Integer.parseInt(((Node)LngList.item(0)).getNodeValue().trim());
		//
		//				LatLng GEOPOSITION = new LatLng(lat,lng);
		//
		//				BitmapDescriptor geoicon = null;
		//
		//				//String geocategory = null;
		//
		//				if ( ((Node)CatList.item(0)).getNodeValue().trim().equalsIgnoreCase("1" )) {
		//					geoicon = BitmapDescriptorFactory.fromResource(R.drawable.recommendation_icon);
		//					//geocategory = getResources().getString(R.string.recommendation);
		//				}
		//				if ( ((Node)CatList.item(0)).getNodeValue().trim().equalsIgnoreCase("2" )) {
		//					geoicon = BitmapDescriptorFactory.fromResource(R.drawable.comment_icon);
		//					//geocategory = getResources().getString(R.string.comment);
		//				}
		//				if ( ((Node)CatList.item(0)).getNodeValue().trim().equalsIgnoreCase("3" )) {
		//					geoicon = BitmapDescriptorFactory.fromResource(R.drawable.special_event_icon);
		//					//geocategory = getResources().getString(R.string.special_event);
		//				}
		//
		//				String geouser = ((Node)UsernameList.item(0)).getNodeValue().trim();
		//				String geotitle = ((Node)TitleList.item(0)).getNodeValue().trim();
		//				String geotext = ((Node)TextList.item(0)).getNodeValue().trim();
		//				String geocreation = ((Node)CreaList.item(0)).getNodeValue().trim();
		//
		//				map.addMarker (new MarkerOptions().position(GEOPOSITION)
		//						.icon(geoicon)
		//						.anchor(0.5f,0.5f)
		//						.alpha(0.5f)
		//						.title(geouser + geotitle + geocreation)
		//						.snippet(geotext)
		//						);
		//
		//				map.setOnMarkerClickListener(new OnMarkerClickListener()
		//				{
		//
		//					@Override
		//					public boolean onMarkerClick(Marker marker) {
		//						Intent intent = new Intent();
		//						intent.setClass(GetLocation.this, ViewGeopost.class);
		//
		//						intent.putExtra("category", getResources().getString(R.string.comment));
		//						intent.putExtra("text", "geopost text blabla blabla bla");
		//						intent.putExtra("title", "geopost title");
		//						intent.putExtra("creation", "geopost creation");
		//						intent.putExtra("user", "geopost user");
		//
		//						startActivity(intent);
		//						return true;
		//					}
		//
		//				});
		//
		//
		//			}; 
		//
		//		};
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return true;
	}


}

