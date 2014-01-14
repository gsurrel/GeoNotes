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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

public class GeoPostServer
{
	public static final int OK = 0;
	public static final int ERROR_UNKNOWN = 1;
	public static final int ERROR_DATA_INVALID = 2;
	public static final int ERROR_NETWORK = 3;
	public static final int ERROR_LOGIN = 4;
	public static final int ERROR_EMAIL_UNIQUE = 5;
	public static final int ERROR_USERNAME_UNIQUE = 6;

	/* Could be handy in the future
	for(int i=1; i<args.length; i++)
	{
		params = params.concat(args[i]);
		if(i<args.length-1)
		{
			params = params.concat("&");
		}
	}
	 */

	private Context context;

	private URL url;
	private JSONArray infos, warnings, errors;
	private JSONObject data;

	public GeoPostServer(Context context)
	{
		this.context = context;
		try
		{
			url = new URL("http://gregoire.surrel.org/gps/?api&");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	private int request(String params) throws JSONException, IOException
	{
		int result = ERROR_UNKNOWN;
		Log.d("gps.request", "Connection");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.getOutputStream().write(params.getBytes());
		Log.d("gps.request", "Set output stream: "+params);
		urlConnection.connect();

		InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		Log.d("gps.request", "Get input stream");

		readStream(in);
		result = OK;

		urlConnection.disconnect();
		Log.d("gps.request", "Disconnect");

		return result;
	}

	public int refreshDB(String login, String password) throws JSONException, IOException
	{
		String params = "action=list&username_email="+login+"&password="+password;
		int result;

		result = request(params);

		// Gets the data repository in write mode and drop current DB
		NotesDbHelper notesDbHelper = new NotesDbHelper(context);
		SQLiteDatabase db = notesDbHelper.getWritableDatabase();
		//notesDbHelper.onUpgrade(db, 0, 0);

		Log.v("gps.refreshDB", this.data.toString());
		
		JSONArray notes = this.data.getJSONArray("data");
		for(int i=0; i<notes.length(); i++)
		{
			// Read JSON note data and insert in DB
			// Create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			JSONArray keys = notes.getJSONObject(i).names();
			for(int j=0; j<keys.length(); j++)
			{
				values.put(keys.getString(j), notes.getJSONObject(i).get(keys.getString(j)).toString());
			}

			// Insert the new row, returning the primary key value of the new row
			long newRowId;
			newRowId = db.insertWithOnConflict("notes", null, values, SQLiteDatabase.CONFLICT_REPLACE);
			if(newRowId == -1)
			{
				// Error occurred
				Log.e("gps.refreshDB", "Could not insert/update in DB");
			}
			else
			{
				Log.v("gps.refreshDB", "Post successfully inserted/updated in DB");
			}
		}

		Log.v("gps.refreshDB", String.valueOf(result));

		return result;
	}

	public int user_login(String login, String password) throws JSONException, IOException
	{
		String params = "action=login&username_email="+login+"&password="+password;
		Log.i("gps.login", "Trying login: "+params);
		int result = request(params);
		Log.i("gps.login", "Result: "+result);

		if(this.infos != null)
			Log.v("gps.login", "Infos: "+this.infos.toString());
		if(this.warnings != null)
			Log.v("gps.login", "Warnings: "+this.warnings.toString());
		if(this.errors != null)
			Log.v("gps.login", "Errors: "+this.errors.toString());
		Log.v("gps.login", "Data: "+this.data.toString());
		
		// Save user details in settings
		if(!this.data.isNull("data"))
		{
			Log.v("gps.login", "Writing settings");
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			Editor settings_editor = settings.edit();
			JSONObject user = this.data.getJSONObject("data");
			settings_editor.putInt("user_id", user.getInt("ID"));
			settings_editor.putString("user_name", user.getString("username"));
			settings_editor.putString("user_email", user.getString("email"));
			// TODO: restore settings from server to app
			settings_editor.apply();
			Log.v("gps.login", "Writing done.");
		}
		else
		{
			result = ERROR_LOGIN;
		}
		
		return result;
	}

	public int user_register(String login, String password, String email) throws JSONException, IOException
	{
		String params = "action=register&username="+login+"&password="+password+"&email="+email;
		Log.i("gps.signup", "Trying signup: "+params);
		int result = request(params);
		Log.i("gps.signup", "Result: "+result);

		if(this.infos != null)
			Log.v("gps.signup", "Infos: "+this.infos.toString());
		if(this.warnings != null)
			Log.v("gps.signup", "Warnings: "+this.warnings.toString());
		if(this.errors != null)
			Log.v("gps.signup", "Errors: "+this.errors.toString());
		Log.v("gps.signup", "Data: "+this.data.toString());
		
		// Save user details in settings
		if(this.errors != null)
		{
			if(this.errors.getString(0) == "Err. user>add : SQLSTATE[23000]: Integrity constraint violation: 19 column username is not unique")
			{
				result = ERROR_USERNAME_UNIQUE;
			}
			else if(this.errors.getString(0) == "Err. user>add : SQLSTATE[23000]: Integrity constraint violation: 19 column email is not unique")
			{
				result = ERROR_EMAIL_UNIQUE;
			}
			else
			{
				result = ERROR_UNKNOWN;
			}
		}
		else
		{	
			result = OK;
		}
		
		return result;
	}

	public int user_update(String key, String value)
	{
		// Can update nickname, email or settings
		return 0;
	}



	private void readStream(InputStream in) throws JSONException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();

		// Get all data
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

		// Parse it
		Log.v("readStream", sb.toString());
		JSONObject json;
		try {
			json = new JSONObject(sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			json = new JSONObject();
		}

		// Fill-in variables
		JSONArray infos;
		infos = (JSONArray) json.getJSONArray("infos");
		for(int i=0; i<infos.length(); i++) {
			Log.i("readStream", infos.optString(i));
		}
		JSONArray warnings = (JSONArray) json.getJSONArray("warnings");
		for(int i=0; i<warnings.length(); i++) {
			Log.w("readStream", warnings.optString(i));
		}
		JSONArray errors = (JSONArray) json.getJSONArray("errors");
		for(int i=0; i<errors.length(); i++) {
			Log.e("readStream", errors.optString(i));
		}
		String[] fields = {"data"};
		this.data = new JSONObject(json, fields);
	}

	public JSONObject getData() {
		return data;
	}

	public JSONArray getErrors() {
		return errors;
	}

	public JSONArray getWarnings() {
		return warnings;
	}

	public JSONArray getInfos() {
		return infos;
	}

}
