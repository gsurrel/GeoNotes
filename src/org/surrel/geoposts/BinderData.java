package org.surrel.geoposts;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BinderData extends BaseAdapter {
	
	LayoutInflater inflater;
	ImageView thumb_image;
	List<HashMap<String,String>> myGeopostsDataCollection;
	ViewHolder holder;
	public BinderData() {
	}
	
	public BinderData(Activity act, List<HashMap<String,String>> map) {
		
		this.myGeopostsDataCollection = map;
		
		inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	public int getCount() {
//		return idlist.size();
		return myGeopostsDataCollection.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 
		View vi=convertView;
	    if(convertView==null){
	     
	      vi = inflater.inflate(R.layout.geopost_list_row, null);
	      holder = new ViewHolder();
	      
	      holder.tvTitle = (TextView)vi.findViewById(R.id.title);
	      holder.tvCreation =(TextView)vi.findViewById(R.id.creation);
	      holder.tvCategory =(ImageView)vi.findViewById(R.id.icon);
	      
	      vi.setTag(holder);
	    }
	    
	    else{
	    	
	    	holder = (ViewHolder)vi.getTag();
	    }

	      holder.tvTitle.setText(myGeopostsDataCollection.get(position).get("title"));
	      holder.tvCreation.setText(myGeopostsDataCollection.get(position).get("creation"));
	      
	      Drawable icon = vi.getContext().getResources().getDrawable(R.drawable.ic_launcher);
	      String categ = myGeopostsDataCollection.get(position).get("category");
	     
	      if (categ.equalsIgnoreCase("1")){
	    	  icon = vi.getContext().getResources().getDrawable(R.drawable.recommendation_icon);
	    	  }
          if (categ.equalsIgnoreCase("2")){
        	  icon = vi.getContext().getResources().getDrawable(R.drawable.comment_icon);
	    	  }
          if (categ.equalsIgnoreCase("3")){
        	  icon = vi.getContext().getResources().getDrawable(R.drawable.special_event_icon);
              }
	    	            
	      holder.tvCategory.setImageDrawable(icon);
	      
	      return vi;
	}

	static class ViewHolder{
		
		ImageView tvCategory;
		TextView tvTitle;
		TextView tvCreation;
	}
	
}
