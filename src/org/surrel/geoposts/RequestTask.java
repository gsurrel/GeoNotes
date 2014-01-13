package org.surrel.geoposts;

import java.io.IOException;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
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
		GeoPostServer gps = new GeoPostServer(context);

		String result;
		Resources res = context.getResources();
		try {
			gps.refreshDB("test", "test");
			result = res.getString(R.string.refresh_ok);
		} catch (JSONException e) {
			result = res.getString(R.string.refresh_data_error);
			Log.e("request", "Error in data received");
		} catch (IOException e) {
			result = res.getString(R.string.refresh_network_error);
			Log.e("request", "Error in network");
		} catch (Exception e) {
			result = res.getString(R.string.refresh_unknown_error);
		}
		
		return result;
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