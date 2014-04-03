package com.github.thiagolocatelli.stripe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class StripeUtils {
	
	public static void removeAllCookies(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	public static Map<String, String> splitQuery(String query) {
	    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
	    try {
		    String[] pairs = query.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), 
		        		URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		    }
	    }
	    catch(UnsupportedEncodingException e) {
	    	query_pairs.put("error", "UnsupportedEncodingException");
	    	query_pairs.put("error_description", e.getMessage());	    	
	    }
	    
	    return query_pairs;
	}	
	
	public static String executePost(String url, String parameters) throws IOException {
		
		URL request = new URL(url); 		
		HttpURLConnection connection = (HttpURLConnection) request.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("User-Agent", "StripeConnectAndroid");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
		connection.setUseCaches (false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
		
		String response = streamToString(connection.getInputStream());	
		connection.disconnect();
		return response;
		
	}
	
	private static String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}
	
}
