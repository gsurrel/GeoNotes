package org.surrel.geoposts;

import java.util.Date;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewGeopost extends Activity{
	
	String category, text, title, user;
	Date creation;
	int id, cat;
	Drawable icon;
	
	TextView tvcategory, tvtitle, tvtext, tvcreation, tvuser;
	ImageView tvicon;
	
	@Override
	public void onBackPressed() {
		finish();
		return;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geopost_details);
        
        tvicon     = (ImageView) findViewById(R.id.geopost_cat_icon);
		tvcategory = (TextView) findViewById(R.id.geopost_cat);
		tvtitle    = (TextView) findViewById(R.id.geopost_title);
		tvtext     = (TextView) findViewById(R.id.geopost_text);
		tvcreation = (TextView) findViewById(R.id.geopost_creation);
		tvuser     = (TextView) findViewById(R.id.geopost_username);
		
        this.id = Integer.parseInt(getIntent().getStringExtra("id"));
        
		try {
	        NotesDbHelper notesDbHelper = new NotesDbHelper(getApplicationContext());
			SQLiteDatabase db = notesDbHelper.getReadableDatabase();
			String[] columns = new String[]{
					"ID", "title",
					"text", "user", "creation",
					"cat"
			};
			
			String[] selectionArgs = new String[]{
					String.valueOf(this.id)
			};
			
			Log.v("Success","get data base");
			Cursor selected_post = db.query(
					"notes",
					columns,
					"ID = ?",
					selectionArgs,
					null, null, null);
			Log.v("Success","filter");
					
			selected_post.moveToFirst();
			Log.v("success","move to first");
			title = selected_post.getString(selected_post.getColumnIndex("title"));
			Log.v("success","set title");
			text = selected_post.getString(selected_post.getColumnIndex("text"));
			Log.v("success","set text");
			user = selected_post.getString(selected_post.getColumnIndex("user"));
			Log.v("success","set user");
			creation = new Date(1000*selected_post.getLong(selected_post.getColumnIndex("creation")));
			Log.v("success","set creation");
			cat = selected_post.getInt(selected_post.getColumnIndex("cat"));
			Log.v("success","set category");
			
		    }
		
		catch (Exception ex) {
			Log.e("Error", "Loading exception");
		}
		
		switch(cat)
		{
		case 0:
			icon = this.getBaseContext().getResources().getDrawable(R.drawable.recommendation_icon);
			category = this.getBaseContext().getResources().getString(R.string.recommendation);
			break;
		case 1:
			icon = this.getBaseContext().getResources().getDrawable(R.drawable.comment_icon);
			category = this.getBaseContext().getResources().getString(R.string.comment);
			break;
		case 2:
			icon = this.getBaseContext().getResources().getDrawable(R.drawable.special_event_icon);
			category = this.getBaseContext().getResources().getString(R.string.special_event);
			break;
		default:
			icon = this.getBaseContext().getResources().getDrawable(R.drawable.ic_launcher);
			category = "no category";
		}
	    
	    tvicon.setImageDrawable(icon);
	    tvtitle.setText(title);
	    tvtext.setText(text);
	    tvuser.setText(user);
	    tvcategory.setText(category);
	    
	    java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        tvcreation.setText(dateFormat.format(creation));
    }
	
}
