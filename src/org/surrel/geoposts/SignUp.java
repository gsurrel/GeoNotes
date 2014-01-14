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

public class SignUp extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
	}

	public void GoToSignUp(View view)
	{
		String result;
		Resources res = getResources();

		Log.d("Act.signup", "Calling signup");
		RequestTask rq = new RequestTask(getApplicationContext());
		rq.execute("signup",
				((EditText)findViewById(R.id.enter_pseudo)).getText().toString(),
				((EditText)findViewById(R.id.enter_password)).getText().toString(),
				((EditText)findViewById(R.id.enter_email)).getText().toString()
				);
		Log.d("Act.signup", "Called signup");

		try {
			result = rq.get(5, TimeUnit.SECONDS);
			Log.d("Act.signup", result);
			if(result == res.getString(R.string.signup_ok))
			{
				Intent toLogIn = new Intent(this, LogIn.class);
				startActivity(toLogIn);
			}
			else
			{
				// Signup failed
			}
		} catch (InterruptedException e) {
			Log.i("Act.signup", "Signup interrupted, InterruptedException");
		} catch (ExecutionException e) {
			Log.i("Act.signup", "Signup aborted, ExecutionException");
			e.printStackTrace();
		} catch (TimeoutException e) {
			Log.i("Act.signup", "Signup failed due to timeout");
			e.printStackTrace();
		}

	}
	
}
