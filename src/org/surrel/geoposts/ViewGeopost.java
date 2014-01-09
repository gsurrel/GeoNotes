package org.surrel.geoposts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewGeopost extends Activity {
	String category = "";
	String creation = "";
	String text = "";
	String title = "";
	String user = "";
	
	TextView tvcategory;
	TextView tvtitle;
	TextView tvtext;
	TextView tvcreation;
	TextView tvuser;
	ImageView tvicon;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geopost_details);
        
		try {
			
			tvicon = (ImageView) findViewById(R.id.geopost_cat_icon);
			tvcategory = (TextView) findViewById(R.id.geopost_cat);
			tvtitle = (TextView) findViewById(R.id.geopost_title);
			tvtext = (TextView) findViewById(R.id.geopost_text);
			tvcreation = (TextView) findViewById(R.id.geopost_creation);
			tvuser = (TextView) findViewById(R.id.geopost_username);
			
	        Intent i = getIntent();
	        
	        this.category = i.getStringExtra("category");
	        this.title = i.getStringExtra("title");
	        this.text =	i.getStringExtra("text");
	        this.creation =  i.getStringExtra("creation");
	        this.user = i.getStringExtra("user");
		
		    //text elements
		    tvtitle.setText(title);
		    tvtext.setText(text);
		    tvcreation.setText(creation);
		    tvuser.setText(user);
		    tvcategory.setText(category);
		    
		    Drawable icon = this.getBaseContext().getResources().getDrawable(R.drawable.ic_launcher);
		    
		    if (category.equalsIgnoreCase(this.getBaseContext().getResources().getString(R.string.recommendation))){
		    	icon = this.getBaseContext().getResources().getDrawable(R.drawable.recommendation_icon);
		    }
		    
		    if (category.equalsIgnoreCase(this.getBaseContext().getResources().getString(R.string.comment))){
		    	icon = this.getBaseContext().getResources().getDrawable(R.drawable.comment_icon);
		    }
		    
		    if (category.equalsIgnoreCase(this.getBaseContext().getResources().getString(R.string.special_event))){
		    	icon = this.getBaseContext().getResources().getDrawable(R.drawable.special_event_icon);
		    }
		    
		    tvicon.setImageDrawable(icon);
		}
		
		catch (Exception ex) {
			Log.e("Error", "Loading exception");
		}
    }
	
}
