package org.surrel.geoposts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class RequestTask extends AsyncTask<String, String, String>{

	Context context;
	
	public RequestTask(Context context)
	{
		this.context = context;
	}
	
	@Override
	protected String doInBackground(String...args) {
		URL url;
		String result = "@string/refresh_network_error";
		try {
			url = new URL(args[0]);
			Log.d("Refresh", "Parsed URL ".concat(url.toString()));
			try {
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				Log.d("Refresh", "Opened connection");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				urlConnection.setUseCaches(false);
				urlConnection.setRequestMethod("POST");
				urlConnection.connect();
				String params = "";
				for(int i=1; i<args.length; i++)
				{
					params = params.concat(args[i]);
					if(i<args.length-1)
					{
						params = params.concat("&");
					}
				}
				Log.v("Refresh", params);
				urlConnection.getOutputStream().write(params.getBytes());

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				Log.d("Refresh", "Get input stream");
				JSONObject json;
				try {
					json = readStream(in);
					Log.v("Refresh", json.toString(4));
					result = "@string/refresh_ok";

					JSONArray infos = (JSONArray) json.get("infos");
					for(int i=0; i<infos.length(); i++) {
						Log.i("Refresh", infos.getString(i));
					}
					JSONArray warnings = (JSONArray) json.get("warnings");
					for(int i=0; i<warnings.length(); i++) {
						Log.w("Refresh", warnings.getString(i));
					}
					JSONArray errors = (JSONArray) json.get("errors");
					for(int i=0; i<errors.length(); i++) {
						Log.e("Refresh", errors.getString(i));
					}

					// Gets the data repository in write mode and drop current DB
					NotesDbHelper notesDbHelper = new NotesDbHelper(context);
					SQLiteDatabase db = notesDbHelper.getWritableDatabase();
					notesDbHelper.onUpgrade(db, 0, 0);

					// Get JSON data
					JSONArray data = (JSONArray) json.getJSONArray("data");
					for(int i=0; i<data.length(); i++)
					{
						// Read JSON note data and insert in DB
						// Create a new map of values, where column names are the keys
						ContentValues values = new ContentValues();
						JSONArray keys = data.getJSONObject(i).names();
						for(int j=0; j<keys.length(); j++)
						{
							values.put(keys.getString(j), data.getJSONObject(i).get(keys.getString(j)).toString());
						}
	
						// Insert the new row, returning the primary key value of the new row
						long newRowId;
						newRowId = db.insertWithOnConflict("notes", null, values, SQLiteDatabase.CONFLICT_REPLACE);
						if(newRowId == -1)
						{
							// Error occurred
							Log.e("Refresh", "Could not insert/update in DB");
						}
						else
						{
							Log.v("Refresh", "Post successfully inserted/updated in DB");
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = "@string/refresh_data_error";
				}
				//Log.v("Refresh", result);
				//Log.d("Refresh", "Got result".concat(result));
				urlConnection.disconnect();
				Log.d("Refresh", "Disconnect");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("Refresh", "Error in block connection");
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("Malformed url", e1.toString());
		}

		return result;
	}

	private JSONObject readStream(InputStream in) {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        JSONObject json;
		try {
			json = new JSONObject(sb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json = new JSONObject();
		}
        
        return json;
	}

	@Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        Log.i("Refresh", result);
        Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
        toast.show();
	}

}