package com.github.thiagolocatelli.stripe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.util.Log;

/**
 *
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * 
 */
public class StripeSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String SHARED = "_StripeAccount_Preferences";
	private static final String API_ACCESS_TOKEN = "access_token";
	private static final String API_REFRESH_TOKEN = "refresh_token";
	private static final String API_TOKEN_TYPE = "token_type";
	private static final String API_USER_ID = "user_id";
	private static final String API_PUBLISHABLE_KEY = "publishable_key";
	private static final String API_LIVE_MODE = "live_mode";

	public StripeSession(Context context, String accountName) {
		Log.i("StripeSession", "StripeSession[accountName]:					" + accountName);
		sharedPref = context.getSharedPreferences(accountName + SHARED, Context.MODE_PRIVATE);
	}

	public void storeAccessToken(String accessToken) {
		editor = sharedPref.edit();
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}
	
	public void storeRefreshToken(String refreshToken) {
		editor = sharedPref.edit();
		editor.putString(API_REFRESH_TOKEN, refreshToken);
		editor.commit();
	}
	
	public void storePublishableKey(String publishableKey) {
		editor = sharedPref.edit();
		editor.putString(API_PUBLISHABLE_KEY, publishableKey);
		editor.commit();
	}
	
	public void storeUserid(String userId) {
		editor = sharedPref.edit();
		editor.putString(API_USER_ID, userId);
		editor.commit();
	}	
	
	public void storeTokenType(String tokenType) {
		editor = sharedPref.edit();
		editor.putString(API_TOKEN_TYPE, tokenType);
		editor.commit();
	}
	
	public void storeLiveMode(boolean liveMode) {
		editor = sharedPref.edit();
		editor.putBoolean(API_LIVE_MODE, liveMode);
		editor.commit();
	}

	public String getAccessToken() {
		return sharedPref.getString(API_ACCESS_TOKEN, null);
	}
	
	public String getRefreshToken() {
		return sharedPref.getString(API_REFRESH_TOKEN, null);
	}
	
	public String getPublishableKey() {
		return sharedPref.getString(API_PUBLISHABLE_KEY, null);
	}
	
	public String getUserId() {
		return sharedPref.getString(API_USER_ID, null);
	}
	
	public String getTokenType() {
		return sharedPref.getString(API_TOKEN_TYPE, null);
	}
	
	public Boolean getLiveMode() {
		return sharedPref.getBoolean(API_LIVE_MODE, false);
	}	

	public void resetAccessToken() {
		editor = sharedPref.edit();
		editor.remove(API_ACCESS_TOKEN);
		editor.remove(API_REFRESH_TOKEN);
		editor.remove(API_PUBLISHABLE_KEY);
		editor.remove(API_USER_ID);
		editor.remove(API_TOKEN_TYPE);
		editor.remove(API_LIVE_MODE);
		editor.clear();
		editor.commit();
	}

}