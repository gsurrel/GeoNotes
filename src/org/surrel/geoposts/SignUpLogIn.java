package org.surrel.geoposts;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SignUpLogIn extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_login);
	}

	@Override
	public void onBackPressed() {
		// Otherwise defer to system default behavior.
		return;
	}

	public void GoToMain(View view)
	{
		String result;
		Resources res = getResources();

		Log.d("Act.login", "Calling login");
		RequestTask rq = new RequestTask(getApplicationContext());
		rq.execute("login",
				((EditText)findViewById(R.id.enter_pseudo)).getText().toString(),
				((EditText)findViewById(R.id.enter_password)).getText().toString()
				);
		Log.d("Act.login", "Called login");

		try {
			result = rq.get(5, TimeUnit.SECONDS);
			Log.d("Act.login", result);
			if(result == res.getString(R.string.login_ok))
			{
				Intent toMain = new Intent(this, MainActivity.class);
				startActivity(toMain);
			}
			else
			{
				// TODO
			}
		} catch (InterruptedException e) {
			Log.i("Act.login", "Login interrupted, InterruptedException");
		} catch (ExecutionException e) {
			Log.i("Act.login", "Login aborted, ExecutionException");
			e.printStackTrace();
		} catch (TimeoutException e) {
			Log.i("Act.login", "Login failed due to timeout");
			e.printStackTrace();
		}

	}

}
