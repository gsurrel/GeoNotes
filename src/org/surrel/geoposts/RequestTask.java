package org.surrel.geoposts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
		Log.d("Refresh", "Caught");
		URL url;
		String result = "Empty";
		try {
			url = new URL(args[0]);
			Log.d("Refresh", "Parsed URL".concat(url.toString()));
			try {
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				Log.d("Refresh", "Opened connection");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				urlConnection.setUseCaches(false);
				urlConnection.setRequestMethod("POST");
				urlConnection.connect();
				byte[] params = "action=login&username_email=test&password=test".getBytes();
				urlConnection.getOutputStream().write(params);

				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				Log.d("Refresh", "Get input stream");
				JSONObject json = readStream(in);
				try {
					result = "Got data:\n".concat(json.toString(4));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = "Got data:\n".concat(json.toString());
				}
				Log.d("Refresh", result);
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
        Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
        toast.show();
	}

}