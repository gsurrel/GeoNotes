package org.surrel.geoposts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyGeoposts extends Activity {

	// List items 
	ListView list;
	BinderData adapter = null;
	List<HashMap<String,String>> myGeopostsDataCollection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_geoposts);

		// Open read access for local database
		NotesDbHelper notesDbHelper = new NotesDbHelper(getApplicationContext());
		SQLiteDatabase db = notesDbHelper.getReadableDatabase();
		String[] columns = new String[]{
				"ID", "lat", "lon", "title",
				"text", "user", "karma", "creation",
				"lifetime", "lang", "cat"
		};

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String name = settings.getString("user_name", "noName");

		String[] selectionArgs = new String[]{
				name
		};

		Log.i("Success", "before filtering");
		Cursor mygeoposts = db.query(
				"notes",
				columns,
				"( user = ? )",
				selectionArgs,
				null, null, null); 
		Log.i("Success","filtering done successfully !!");

		HashMap<String,String> mygeomap = null;

		final List<HashMap<String,String>> myGeopostsDataCollection = 
				new ArrayList<HashMap<String,String>>();
		if(mygeoposts.moveToFirst())
		{
			do
			{
				mygeomap = new HashMap<String,String>(); 

				int cat = mygeoposts.getInt(mygeoposts.getColumnIndex("cat"));
				String categ = String.valueOf(cat + 1);
				Date date = new Date(1000*mygeoposts.getLong(mygeoposts.getColumnIndex("creation")));
				mygeomap.put("category", categ);
				mygeomap.put("title", mygeoposts.getString(mygeoposts.getColumnIndex("title")));
				mygeomap.put("text", mygeoposts.getString(mygeoposts.getColumnIndex("text")));
				mygeomap.put("creation", String.valueOf(date));
				mygeomap.put("ID", mygeoposts.getString(mygeoposts.getColumnIndex("ID")));
				myGeopostsDataCollection.add(mygeomap);	
			} 
			while(mygeoposts.moveToNext());
		}

		BinderData bindingData = new BinderData(this,myGeopostsDataCollection);

		list = (ListView) findViewById(R.id.geopost_list);

		list.setAdapter(bindingData);

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				Intent i = new Intent();
				i.setClass(MyGeoposts.this, MyGeoPostDetails.class);

				i.putExtra("position", String.valueOf(position + 1));
				i.putExtra("category", myGeopostsDataCollection.get(position).get("category"));
				i.putExtra("text", myGeopostsDataCollection.get(position).get("text"));
				i.putExtra("title", myGeopostsDataCollection.get(position).get("title"));
				i.putExtra("creation", myGeopostsDataCollection.get(position).get("creation"));
				i.putExtra("ID", myGeopostsDataCollection.get(position).get("ID"));

				// start Sample Activity
				startActivity(i);
			}
		});
	}
}
