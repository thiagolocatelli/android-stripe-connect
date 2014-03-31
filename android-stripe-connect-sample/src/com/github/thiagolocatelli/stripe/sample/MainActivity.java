package com.github.thiagolocatelli.stripe.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.thiagolocatelli.stripe.StripeApp;
import com.github.thiagolocatelli.stripe.StripeApp.CONNECT_MODE;
import com.github.thiagolocatelli.stripe.StripeButton;
import com.github.thiagolocatelli.stripe.StripeConnectListener;

public class MainActivity extends ActionBarActivity {

	private StripeApp mApp, mApp2;
	private TextView tvSummary;
	private StripeButton mStripeButton, mStripeButton2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mApp = new StripeApp(this, ApplicationData.CLIENT_ID, 
				ApplicationData.SECRET_KEY, ApplicationData.CALLBACK_URL);

		tvSummary = (TextView) findViewById(R.id.tvSummary);
		if (mApp.isConnected()) {
			tvSummary.setText("Connected as " + mApp.getAccessToken());
		}
		
		mStripeButton = (StripeButton) findViewById(R.id.btnConnect1);
		mStripeButton.setStripeApp(mApp);
		mStripeButton.addStripeConnectListener(new StripeConnectListener() {

			@Override
			public void onConnected() {
				tvSummary.setText("Connected as " + mApp.getAccessToken());
			}

			@Override
			public void onDisconnected() {
				tvSummary.setText("Disconnected");
			}

			@Override
			public void onError(String error) {
				Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
			}
			
		});

		mApp2 = new StripeApp(this, ApplicationData.CLIENT_ID, 
				ApplicationData.SECRET_KEY, ApplicationData.CALLBACK_URL);
		mStripeButton2 = (StripeButton) findViewById(R.id.btnConnect2);
		mStripeButton2.setStripeApp(mApp2);
		mStripeButton2.setConnectMode(CONNECT_MODE.ACTIVITY);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(resultCode) {
		case StripeApp.RESULT_CONNECTED:
			tvSummary.setText("Connected as " + mApp.getAccessToken());
			break;
		case StripeApp.RESULT_ERROR:
			String error_description = data.getStringExtra("error_description");
			Toast.makeText(MainActivity.this, error_description, Toast.LENGTH_SHORT).show();
			break;
		}
		
	}


}
