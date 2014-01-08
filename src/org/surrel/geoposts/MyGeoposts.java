package org.surrel.geoposts;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;
	
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Log.v("tag1","1");
			e.printStackTrace();
		}
		
		try {
			doc = docBuilder.parse (getAssets().open("my_geoposts.xml"));
		} catch (SAXException e) {
			Log.v("tag2","1");
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("tag2","2");
			e.printStackTrace();
		}


		final List<HashMap<String,String>> myGeopostsDataCollection = 
		           new ArrayList<HashMap<String,String>>();
		
		doc.getDocumentElement ().normalize ();
		                        
		NodeList mygeopostsList = doc.getElementsByTagName("mydata");
		            
		HashMap<String,String> mygeomap = null;
		            
		for (int i = 0; i < mygeopostsList.getLength(); i++) {
		             
		    mygeomap = new HashMap<String,String>(); 
		               
		    Node firstgeopostNode = mygeopostsList.item(i);
		               
		       if(firstgeopostNode.getNodeType() == Node.ELEMENT_NODE){

		         Element firstgeopostElement = (Element)firstgeopostNode;
		             
		         NodeList catList = firstgeopostElement.getElementsByTagName("category");
		         Element firstCatElement = (Element)catList.item(0);
		         NodeList CatList = firstCatElement.getChildNodes();
		         mygeomap.put("category", ((Node)CatList.item(0)).getNodeValue().trim());
		                    
		         
		         NodeList titleList = firstgeopostElement.getElementsByTagName("title");
		         Element firstTitleElement = (Element)titleList.item(0);
		         NodeList TitleList = firstTitleElement.getChildNodes();
		         mygeomap.put("title", ((Node)TitleList.item(0)).getNodeValue().trim());
		                        
		         NodeList textList = firstgeopostElement.getElementsByTagName("text");
		         Element firstTextElement = (Element)textList.item(0);
		         NodeList TextList = firstTextElement.getChildNodes();
		         mygeomap.put("text", ((Node)TextList.item(0)).getNodeValue().trim());
		                    
		         NodeList creaList = firstgeopostElement.getElementsByTagName("creation");
		         Element firstCreaElement = (Element)creaList.item(0);
		         NodeList CreaList = firstCreaElement.getChildNodes();
		         mygeomap.put("creation", ((Node)CreaList.item(0)).getNodeValue().trim());
		         
		         myGeopostsDataCollection.add(mygeomap);
		    }        
		}
		
		BinderData bindingData = new BinderData(this,myGeopostsDataCollection);

		
		list = (ListView) findViewById(R.id.geopost_list);

		Log.i("BEFORE", "<<------------- Before SetAdapter-------------->>");

		list.setAdapter(bindingData);

		Log.i("AFTER", "<<------------- After SetAdapter-------------->>");

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(MyGeoposts.this, SampleActivity.class);
				
				i.putExtra("position", String.valueOf(position + 1));
				
				i.putExtra("category", myGeopostsDataCollection.get(position).get("category"));
				i.putExtra("text", myGeopostsDataCollection.get(position).get("text"));
				i.putExtra("title", myGeopostsDataCollection.get(position).get("title"));
				i.putExtra("creation", myGeopostsDataCollection.get(position).get("creation"));

				// start the sample activity
				startActivity(i);
			}
		});
	}

}
