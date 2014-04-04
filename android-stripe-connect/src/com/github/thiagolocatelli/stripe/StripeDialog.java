package com.github.thiagolocatelli.stripe;

import java.util.LinkedHashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Display Stripe Connect authentication dialog.
 * 
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * 
 */
public class StripeDialog extends Dialog {

	static final int[] DIMENSIONS_LANDSCAPE = { 60, 60 };
	static final int[] DIMENSIONS_PORTRAIT = { 30, 60 };
	
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;

	private String mUrl;
	private String mCallbackUrl;
	
	private OAuthDialogListener mListener;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;

	private static final String TAG = "StripeConnectAPI";

	public StripeDialog(Context context, String url, String callbackUrl, OAuthDialogListener listener) {
		super(context);
		mUrl = url;
		mCallbackUrl = callbackUrl;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		setUpTitle();
		setUpWebView();

		int pixelsWidth = getScreenWidthPixes();
		int pixelsHeight = getScreenHeightPixes();
		int dpWidth = pxToDp(pixelsWidth);
		int dpHeight = pxToDp(pixelsHeight);
		
		int[] dimensions = (pixelsWidth < pixelsHeight) ? DIMENSIONS_PORTRAIT
						: DIMENSIONS_LANDSCAPE;
		
		int dpNewWidth = dpWidth - dimensions[0];
		int dpNewHeight = dpHeight - dimensions[1];
		int pixelsNewWidth = dpToPx(dpNewWidth);
		int pixelsNewHeight = dpToPx(dpNewHeight);
		addContentView(mContent, new FrameLayout.LayoutParams(pixelsNewWidth, pixelsNewHeight));
		
		StripeUtils.removeAllCookies(getContext());
	}

	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mTitle = new TextView(getContext());
		mTitle.setText("Stripe Connect");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(Color.BLACK);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mContent.addView(mTitle);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebView() {
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new OAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mContent.addView(mWebView);
	}

	private class OAuthWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			AppLog.d(TAG, "OAuthWebViewClient.shouldOverrideUrlLoading", "Redirecting URL " + url);

			if (url.startsWith(mCallbackUrl)) {
				
				String queryString = url.replace(mCallbackUrl + "/?", "");
				AppLog.d(TAG, "OAuthWebViewClient.shouldOverrideUrlLoading", "queryString:" + queryString);
				Map<String, String> parameters = StripeUtils.splitQuery(queryString);
				if(!url.contains("error")) {
					mListener.onComplete(parameters);
				}
				else {
					mListener.onError(parameters);
				}
				StripeDialog.this.dismiss();
				return true;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			AppLog.e(TAG, "OAuthWebViewClient.onReceivedError", "Page error[errorCode="+errorCode+"]: " + description);

			super.onReceivedError(view, errorCode, description, failingUrl);
			Map<String, String> error = new LinkedHashMap<String, String>();
			error.put("error", String.valueOf(errorCode));
			error.put("error_description", description);
			mListener.onError(error);
			StripeDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			AppLog.d(TAG, "OAuthWebViewClient.onPageStarted", "url: " + url);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			AppLog.d(TAG, "OAuthWebViewClient.onPageFinished", "url: " + url);
			mSpinner.dismiss();
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}
		}

	}

	public interface OAuthDialogListener {
		public abstract void onComplete(Map<String, String> parameters);
		public abstract void onError(Map<String, String> parameters);
	}
	
	@SuppressLint("InlinedApi")
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}

	@SuppressLint("InlinedApi")
	public int pxToDp(int px) {
	    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private int getScreenWidthPixes() {
		Point size = new Point();
		WindowManager w = getWindow().getWindowManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
		    w.getDefaultDisplay().getSize(size);
		    return size.x;
		}
		else {
		    Display d = w.getDefaultDisplay();
		    return d.getWidth();
		}		
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private int getScreenHeightPixes() {
		Point size = new Point();
		WindowManager w = getWindow().getWindowManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
		    w.getDefaultDisplay().getSize(size);
		    return size.y;
		}
		else {
		    Display d = w.getDefaultDisplay();
		    return d.getHeight();
		}		
	}

}