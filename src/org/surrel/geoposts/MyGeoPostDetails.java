package org.surrel.geoposts;

import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MyGeoPostDetails extends Activity {
	
	String category = "";
	String creation = "";
	String text = "";
	String title = "";
	String ID = "";
	
	TextView tvcategory;
	TextView tvtitle;
	TextView tvtext;
	TextView tvcreation;
	ImageView tvicon;
	
	private Button Delete;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geopost_details);
        
        Delete = (Button) findViewById(R.id.delete_button);
	    Delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancel_dialog = new AlertDialog.Builder(MyGeoPostDetails.this);
				cancel_dialog.setMessage(R.string.message_delete)
				       .setTitle(R.string.title_delete)
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
        
		try {
			
			tvicon = (ImageView) findViewById(R.id.detail_cat_icon);
			tvcategory = (TextView) findViewById(R.id.detail_cat);
			tvtitle = (TextView) findViewById(R.id.detail_title);
			tvtext = (TextView) findViewById(R.id.detail_text);
			tvcreation = (TextView) findViewById(R.id.detail_creation);
			
			
			// Get position to display
	        Intent i = getIntent();
	        
	        this.category = i.getStringExtra("category");
	        this.title = i.getStringExtra("title");
	        this.text =	i.getStringExtra("text");
	        this.creation =  i.getStringExtra("creation");
	        this.ID =  i.getStringExtra("ID");
		
		    //text elements
		    tvtitle.setText(title);
		    tvtext.setText(text);
		    tvcreation.setText(creation);
		    
		    Drawable icon = this.getBaseContext().getResources().getDrawable(R.drawable.ic_launcher);
		    String cat = "default";
		    
		    if (category.equalsIgnoreCase("1")){
		    	cat = this.getBaseContext().getResources().getString(R.string.recommendation);
		    	icon = this.getBaseContext().getResources().getDrawable(R.drawable.recommendation_icon);
		    }
		    
		    if (category.equalsIgnoreCase("2")){
		    	cat = this.getBaseContext().getResources().getString(R.string.comment);
		    	icon = this.getBaseContext().getResources().getDrawable(R.drawable.comment_icon);
		    }
		    
		    if (category.equalsIgnoreCase("3")){
		    	cat = this.getBaseContext().getResources().getString(R.string.special_event);
		    	icon = this.getBaseContext().getResources().getDrawable(R.drawable.special_event_icon);
		    }
		    
		    tvcategory.setText(cat);
		    tvicon.setImageDrawable(icon);
		}
		
		catch (Exception ex) {
			Log.e("Error", "Loading exception");
		}
    }
    
    public void BackToMain()
    {
    	Log.d("Act.post.remove", "Called post remove");
    	RequestTask rq = new RequestTask(getApplicationContext());
    	rq.execute("remove", this.ID);
    	Log.d("Act.post.remove", "Called post remove");

    	Intent back_to_main  = new Intent(this, MyGeoposts.class);
    	startActivity(back_to_main);
    }
}