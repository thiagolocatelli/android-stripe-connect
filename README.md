android-stripe-connect
======================

Android Library for integrating Stripe using Stripe Connect Oauth

->![Stripe Connect](http://i.imgur.com/dmDuzWE.png "Stripe Connect")<-


## Usage

You can create an utility class where you can define your application credentials, like the one below:


```XML
	<com.github.thiagolocatelli.stripe.StripeButton
		android:id="@+id/btnConnect1"
		android:layout_height="wrap_content"
		android:layout_width="200dip" 
		android:layout_gravity="center_horizontal"
		android:layout_marginTop="20dip"/>
```



```Java
public class ApplicationData {
	public static final String CLIENT_ID = "";
	public static final String CLIENT_SECRET = "";
	public static final String CALLBACK_URL = "";
}
```



```Java
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
```

By default, when the Stripe Connect button is clicked, an Android Dialog will open and display the Stripe authentication page. If you would like to open an Activity instead of a Dialog, you can use **setConnectMode** to change such behavior.

```Java
mStripeButton2 = (StripeButton) findViewById(R.id.btnConnect2);
mStripeButton2.setStripeApp(mApp2);
mStripeButton2.setConnectMode(CONNECT_MODE.ACTIVITY);
```
You also need to add to your AndroidManifest.xml the following line, which will allow the Stripe Connect button to start the authentication Activity.

```XML
<activity android:name="com.github.thiagolocatelli.stripe.StripeActivity"  />
```

Once the authentication is finished, you can use the helper methods from the object StripeApp to get the data you need, like the oauth access token required to make calls using the [Stripe Java library](https://github.com/stripe/stripe-java).

```JAVA
Stripe.apiKey = mApp.getAccessToken();
```
