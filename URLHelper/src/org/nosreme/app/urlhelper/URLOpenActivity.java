package org.nosreme.app.urlhelper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class URLOpenActivity extends ListActivity {
	private final int REQ_CHOOSE_ACTION = 0;

	private String intentOptionsStr = null;

	/* Launch an item, expanding if configured and necessary. */
	/* Launch a URL using the configured browser. */
	private void launchUrl(String urlString)
	{
		/* TODO: switch to IntentResolver if still needed */
    	PackageManager pm = getPackageManager();
    	Uri uri = Uri.parse(urlString);
    	Intent intent = new Intent();
    	intent.setComponent(null);
    	
    	intent.setData(uri);
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	
    	/* Ask the system for possible activities which will handle this URL.
    	 * This is likely to include us, so we want to filter it out.
    	 */
    	List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
    	String pkg = null;
    	String name = null;
    	Log.i("URLHelper", "Searching for handler for " + urlString);
    	
    	int activityCount = activities.size();
    	Parcelable[] activitylist = new Parcelable[activityCount];
    	// Build a list of the options as strings
    	String[] intentStrList = new String[activityCount];
    	int activitiesFound = 0;
    	for (ResolveInfo ri: activities)
    	{
    		ActivityInfo ai = ri.activityInfo;
    		Log.i("URLHelper", "Activity: " + ai.packageName + "/" + ai.applicationInfo.className 
    				+ "/" + ai.name);
    		if (!ai.name.startsWith("org.nosreme.app.urlhelper"))
    		{
    			/* It's an activity which isn't this one, so add it to the list. */
    			if (pkg == null)
    			{
    				/* If it's the first interesting one, save the details. */
    			    pkg = ai.packageName;
    			    name = ai.name;
    			}
    			Intent actIntent = new Intent();
    			actIntent.setClassName(ai.packageName, ai.name);
    			activitylist[activitiesFound] = actIntent;
    			intentStrList[activitiesFound] = ai.packageName + "/" + ai.name;
    			activitiesFound += 1;
    		}
    	}
    	
    	boolean chosen = false;
		String combinedActivityList = null;
		
    	if (activitiesFound > 1)
    	{
    		/* Build a single canonical string of the possible handlers,
    		 * which we can store and look up in the database.
    		 */
    		Arrays.sort(intentStrList, 0, activitiesFound);
    		
    		StringBuilder builder = new StringBuilder();
    		
    		for (int i=0; i<activitiesFound; ++i)
    		{
    			builder.append("@@");
    			builder.append(intentStrList[i]);
    		}
    		combinedActivityList = new String(builder);
    		
    		UrlStore urlstore = new UrlStore(getApplicationContext());

    	    Cursor cursor = urlstore.findHandlerSet(combinedActivityList);
    	    
    	    Log.v("handler", "combined str=" + combinedActivityList);
    	    
    	    cursor.moveToFirst();
    	    if (!cursor.isAfterLast())
    	    {
    	    	/* TODO: Use constants for column names!*/
    	    	pkg = cursor.getString(2);
    	    	name = cursor.getString(3);
    	    	Log.v("handler","Found: " + pkg + "/" + name);
    	    	chosen = true;
    	    }
    	    else
    	    {
    	    	Log.v("handler", "Nothing found");
    	    }
    	}
    	else
    	{
    		/* There's only one, so we're ok. */
    		chosen = true;
    	}
    	
    	/* If there's no single choice, then ask the user. */
    	if (!chosen)
    	{
    		Intent chooserIntent = new Intent();
    		chooserIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
    		if (false) {
    			/* TODO:
    			 * This is a way to add activities to the chooser; but to
    			 * filter out there's not much I can do without re-implementing
    			 * the chooser.  Some useful references when I get around to doing
    			 * that:
    			 * 
    			 * http://pilcrowpipe.blogspot.co.uk/2012/01/creating-custom-android-intent-chooser.html
    			 * http://stackoverflow.com/questions/5734678/custom-filtering-of-intent-chooser-based-on-installed-android-package-name
    			 */
    		    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, activitylist);
    		}

    		chooserIntent.putExtra(Intent.EXTRA_INTENT, intent);
    		
    		/* This will create the system chooser, and return the result in onActivityResult
    		 * below (when we'll actually launch it).
    		 */
    		intentOptionsStr = combinedActivityList;
    		Log.v("handler", "starting chooser.  saved str:" + intentOptionsStr);
    		//startActivityForResult(chooserIntent, REQ_CHOOSE_INTENT);    		
    	} else {
    		/* Only one, so use it directly. */
    	    intent.setClassName(pkg, name);
        	startActivity(intent);
    	}
	}

	private static final int MAX_URL_EXPAND = 5;

	/*
	 * Attempt to expand a shortened URL. Returns null if there is no change.
	 * 
	 * If iterate==true, then iterates until there is no further redirection. To
	 * avoid loops, at most MAX_URL_EXPAND iterations.
	 */
	private static String expandUrl(String urlString, boolean iterate) {
		URL url;
		String result = null;
		int iterations = iterate ? MAX_URL_EXPAND : 1;
		do {
			try {
				url = new URL((result != null) ? result : urlString);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("HEAD");
				conn.setInstanceFollowRedirects(false);
				// See http://code.google.com/p/android/issues/detail?id=16227
				// and http://code.google.com/p/android/issues/detail?id=24672
				// for why the Accept-Encoding is required to disable gzip
				conn.setRequestProperty("Accept-Encoding", "identity");
				int resp = conn.getResponseCode();
				if (resp == 301 || resp == 302) {
					Map<String, List<String>> headers = conn.getHeaderFields();
					List<String> hlist = headers.get("location");
					if (hlist.size() != 1) {
						return result;
					}
					result = hlist.get(0);
				} else {
					return result;
				}
			} catch (Exception e) {
				return result;
			}
		} while (--iterations > 0);

		return result;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Check whether we're in offline mode. */
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		UrlStore urlstore = new UrlStore(getApplicationContext());

		Intent intent = getIntent();
		
		boolean willLaunch = isOnline(prefs);  /* We're online and have a new URL */

		if (!willLaunch) {
			/*
			 * We're going to save the URL and show the list. TODO: A
			 * "save but don't show" option might be good.
			 */
			urlstore.addUrl(intent.getDataString());

			Toast t2 = Toast.makeText(getApplicationContext(), "URLHelper: URL saved.", Toast.LENGTH_SHORT);
			t2.show();
			finish();
		} else {		/* If online, simply relaunch it. */
			intent.setClass(getApplicationContext(), ActionChooser.class);
			// Clear any flags (such as FORWARD!) that we may not want.
			intent.setFlags(0);
			startActivityForResult(intent, REQ_CHOOSE_ACTION);
		}
	}
	
	/* Check whether we're online or not. */
	private boolean isOnline(SharedPreferences prefs) {
		boolean online;
		String launchSetting = prefs.getString("launchimm", "never");
		/* Carefully check the easy ones first; this means that if, say, a
		 * CyanogenMod user disables the ACCESS_NETWORK_STATE permission we
		 * won't crash if it's unavailable.
		 */
		if (launchSetting.equals("never"))
		{
			online = false;
		}
		else if (launchSetting.equals("always"))
		{
		    online = true;
		}
		else /* launchimm == wifi */
		{
			Context context = getApplicationContext();
			ConnectivityManager cman = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cman.getActiveNetworkInfo();
			if ((info != null) &&
			    (info.getType() == ConnectivityManager.TYPE_WIFI) &&
			    info.isConnected())
			{
			    online = true;
			}
			else
			{
				online = false;
			}
			
		}
		return online;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("handler", "in onActivityResult");
		if (requestCode == REQ_CHOOSE_ACTION) {
			Log.i("URLHandler", "Got activity result");
			Log.v("handler", "got result, saved str=" + intentOptionsStr);
			Log.v("handler", "got resultCode" + resultCode);
			switch (resultCode)
			{
			case ActionChooser.RESULT_OPEN:
			    {
			    	/* If we've been asked to open, then do so. */
			    	startActivity((Intent)data.getParcelableExtra(ActionChooser.EXTRA_INTENT));
			    	finish();
			    	break;
			    }
			case ActionChooser.RESULT_EXPAND:
			    {
				    String url = data.getDataString();
				    String expanded = expandUrl(url, true);
				    
				    launchUrl(expanded);
				    break;
			    }
			}
		}
	}
}