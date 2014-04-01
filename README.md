android-stripe-connect
======================

Android Library for integrating with Stripe using [Stripe Connect Oauth](https://stripe.com/docs/connect/reference).

<center>
![Stripe Connect](http://i.imgur.com/vnoicS1.png "Stripe Connect")
![Stripe Connect](http://i.imgur.com/oyW4Phj.png "Stripe Connect")
</center>

## Usage

You can add the Stripe Connect button to your layout using the following XML code:


```XML
	<com.github.thiagolocatelli.stripe.StripeButton
		android:id="@+id/btnStripeConnect"
		android:layout_height="wrap_content"
		android:layout_width="200dip" 
		android:layout_gravity="center_horizontal"
		android:layout_marginTop="20dip"/>
```

You can create an utility class where you can define your application credentials, like the one below (This is obvilously insecure, make sure you keep all this information stored in a way its impossible to decompile):

```Java
public class ApplicationData {
	public static final String CLIENT_ID = "";
	public static final String CLIENT_SECRET = "";
	public static final String CALLBACK_URL = "";
}
```

Inside your Activity, you can manipulate the button and change its properties. You can either launch a Dialog to start the authentication or start a Activity.

```Java
StripeAppmApp = new StripeApp(this, ApplicationData.CLIENT_ID, 
				ApplicationData.SECRET_KEY, ApplicationData.CALLBACK_URL);

mStripeButton = (StripeButton) findViewById(btnStripeConnect);
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
## Contact

If you have any questions, please drop me a line: "thiago:locatelli$gmail:com".replace(':','.').replace('$','@')

