android-stripe-connect
======================

Android Library for integrating with Stripe using [Stripe Connect Oauth](https://stripe.com/docs/connect/reference).

<center>
![Stripe Connect](https://github.com/thiagolocatelli/android-stripe-connect/raw/master/resources/screenshots/screenshot1.png "Stripe Connect")
![Stripe Connect](https://github.com/thiagolocatelli/android-stripe-connect/raw/master/resources/screenshots/screenshot2.png "Stripe Connect")
![Stripe Connect](https://github.com/thiagolocatelli/android-stripe-connect/raw/master/resources/screenshots/screenshot3.png "Stripe Connect")
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

Inside your Activity, you can manipulate the button and change its properties. You can either launch a Dialog to start the authentication or start a Activity. By default, the scope is **read_only**, if you want to give write permissions, you gotta use StripeApp object and pass "read_write" as scope.

```Java
StripeAppmApp = new StripeApp(this, ApplicationData.CLIENT_ID, 
				ApplicationData.SECRET_KEY, ApplicationData.CALLBACK_URL);

mStripeButton = (StripeButton) findViewById(R.id.btnStripeConnect);
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
mStripeButton = (StripeButton) findViewById(R.id.btnStripeConnect);
mStripeButton.setStripeApp(mApp);
mStripeButton.setConnectMode(CONNECT_MODE.ACTIVITY);
```
You also need to add to your AndroidManifest.xml the following line, which will allow the Stripe Connect button to start the authentication Activity.

```XML
<activity android:name="com.github.thiagolocatelli.stripe.StripeActivity"  />
```

Once the authentication is finished, you can use the helper methods from the object StripeApp to get the data you need, like the oauth access token required to make calls using the [Stripe Java library](https://github.com/stripe/stripe-java).

```JAVA
Stripe.apiKey = mApp.getAccessToken();
```

Download the sample application and git it a try.

License
=======

    Copyright 2013, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


## Contact

If you have any questions, please drop me a line: "thiago:locatelli$gmail:com".replace(':','.').replace('$','@')

