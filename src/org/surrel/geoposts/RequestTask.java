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
		Resources res = context.getResources();
		if(args.length != 3)
		{
			// We expect 3 arguments:
			//  action, login, password
			return res.getString(R.string.wrong_request);
		}
		GeoPostServer gps = new GeoPostServer(context);

		String result;
		try {
			if(args[0] == "updateDB")
			{
				gps.refreshDB(args[1], args[2]);
				result = res.getString(R.string.refresh_ok);
			}
			else if(args[0] == "login")
			{
				int tmp = gps.user_login(args[1], args[2]);
				if(tmp == GeoPostServer.OK)
				{
					result = res.getString(R.string.login_ok);
				}
				else
				{
					result = res.getString(R.string.login_error);
				}
			}
			else
			{
				result = res.getString(R.string.unknown_request);
			}
		} catch (JSONException e) {
			result = res.getString(R.string.refresh_data_error);
			Log.e("request", "Error in data received");
			e.printStackTrace();
		} catch (IOException e) {
			result = res.getString(R.string.refresh_network_error);
			Log.e("request", "Error in network");
			e.printStackTrace();
		} catch (Exception e) {
			result = res.getString(R.string.refresh_unknown_error);
			Log.wtf("request", "Unexpected error");
			e.printStackTrace();
		}
		
		return result;
	}


	@Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        Log.i("request", result);
        Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
        toast.show();
	}

}