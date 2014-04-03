package com.github.thiagolocatelli.stripe;

import java.net.URL;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.github.thiagolocatelli.stripe.StripeDialog.OAuthDialogListener;
import com.stripe.Stripe;
import com.stripe.model.Account;

/**
 * 
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * 
 */
public class StripeApp {
	
	public static enum CONNECT_MODE { DIALOG, ACTIVITY }
	
	public static final int STRIPE_CONNECT_REQUEST_CODE = 1;
	public static final int RESULT_CONNECTED = 1;
	public static final int RESULT_ERROR = 2;
	
	private StripeSession mSession;
	private StripeDialog mDialog;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;

	private String mCallbackUrl;
	private String mAuthUrl;
	private String mSecretKey;
	private String mAccountName;

	private static int SUCCESS = 0;
	private static int ERROR = 1;
	private static int PHASE1 = 1;
	private static int PHASE2 = 2;
	private static final String AUTH_URL = "https://connect.stripe.com/oauth/authorize?";
	private static final String TOKEN_URL = "https://connect.stripe.com/oauth/token";
	private static final String SCOPE = "read_only";
	
	private static final String TAG = "StripeApp";
	
	public StripeApp(Context context, String accountName, String clientId, String clientKey, String callbackUrl, String scope) {
		mSession = new StripeSession(context, accountName);
		mAccountName = accountName;
		mSecretKey = clientKey;
		mCallbackUrl = callbackUrl;
		mAuthUrl = AUTH_URL + "client_id=" + clientId
				+ "&scope=" + ((scope == null) ? SCOPE : scope)  
				+ "&response_type=code"
				+ "&stripe_landing=login" 
				+ "&redirect_uri="+ mCallbackUrl ;

		OAuthDialogListener listener = new OAuthDialogListener() {
			@Override
			public void onComplete(Map<String, String> parameters) {
				getAccessToken(parameters.get("code"));
			}

			@Override
			public void onError(Map<String, String> parameter) {
				mListener.onFail("Authorization failed");
			}
		};

		mDialog = new StripeDialog(context, mAuthUrl, mCallbackUrl, listener);
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);		
	}

	public StripeApp(Context context, String accountName, String clientId, String clientKey, String callbackUrl) {
		this(context, accountName, clientId, clientKey, callbackUrl, null);
	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Connecting with Stripe");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				int what = SUCCESS;

				try {

					URL url = new URL(TOKEN_URL); 
					String urlParameters = "code=" + code 
							+ "&client_secret=" + mSecretKey
							+ "&grant_type=authorization_code"; 
					AppLog.i(TAG, "getAccessToken", "Getting access token with code:" + code);
					AppLog.i(TAG, "getAccessToken", "Opening URL " + url.toString() + "?" + urlParameters);
					
					String response = StripeUtils.executePost(TOKEN_URL, urlParameters);
					JSONObject obj = new JSONObject(response);
					
					AppLog.i(TAG, "getAccessToken", "String data[access_token]:			" + obj.getString("access_token"));
					AppLog.i(TAG, "getAccessToken", "String data[livemode]:				" + obj.getBoolean("livemode"));
					AppLog.i(TAG, "getAccessToken", "String data[refresh_token]:			" + obj.getString("refresh_token"));
					AppLog.i(TAG, "getAccessToken", "String data[token_type]:			" + obj.getString("token_type"));
					AppLog.i(TAG, "getAccessToken", "String data[stripe_publishable_key]: " + obj.getString("stripe_publishable_key"));
					AppLog.i(TAG, "getAccessToken", "String data[stripe_user_id]:		" + obj.getString("stripe_user_id"));
					AppLog.i(TAG, "getAccessToken", "String data[scope]:					" + obj.getString("scope"));
					
					mSession.storeAccessToken(obj.getString("access_token"));
					mSession.storeRefreshToken(obj.getString("refresh_token"));
					mSession.storePublishableKey(obj.getString("stripe_publishable_key"));
					mSession.storeUserid(obj.getString("stripe_user_id"));
					mSession.storeLiveMode(obj.getBoolean("livemode"));
					mSession.storeTokenType(obj.getString("token_type"));
					
				} 
				catch (Exception ex) {
					what = ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, PHASE2, 0));
			}
		}.start();
	}

	private void getAccountData() {
		mProgress.setMessage("Finalizing ...");

		new Thread() {
			@Override
			public void run() {
				AppLog.i(TAG, "getAccountData", "Fetching user info");
				int what = SUCCESS;

				try {
					
					Stripe.apiKey = mSession.getAccessToken();
					Account account = Account.retrieve();

					if(account != null) {
						
					}
					
				} catch (Exception ex) {
					what = ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, PHASE2, 0));
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == PHASE1) {
				if (msg.what == SUCCESS) {
					getAccountData();
				} else {
					mProgress.dismiss();
					mListener.onFail("Failed to get access token");
				}
			} else {
				AppLog.i(TAG, "mHandler.handleMessage", "Calling mListener.onSuccess()");
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};

	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}
	
	public OAuthAuthenticationListener getOAuthAuthenticationListener() {
		return mListener;
	}

	public void displayDialog() {
		mDialog.show();
	}

	public void resetAccessToken() {
		if (isConnected()) {
			mSession.resetAccessToken();
			mListener.onSuccess();
		}
	}

	public boolean isConnected() {
		return getAccessToken() != null;
	}

	public String getAccessToken() {
		return mSession.getAccessToken();
	}
	
	public StripeSession getStripeSession() {
		return mSession;
	}
	
	public String getAccountName() {
		return mAccountName;
	}
	
	protected String getAuthUrl() {
		return mAuthUrl;
	}
	
	protected String getCallbackUrl() {
		return mCallbackUrl;
	}
	
	protected String getTokenUrl() {
		return TOKEN_URL;
	}
	
	protected String getSecretKey() {
		return mSecretKey;
	}
	
	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}
	
}