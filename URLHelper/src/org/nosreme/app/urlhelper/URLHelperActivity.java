package org.nosreme.app.urlhelper;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import org.nosreme.app.urlhelper.UrlStore;

public class URLHelperActivity extends ListActivity {
	private final String[] colFields = { "URL" };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        UrlStore urlstore = new UrlStore(getApplicationContext());

        Intent intent = getIntent();
        if (intent.getAction().equals(android.content.Intent.ACTION_VIEW))
        {
        	urlstore.addUrl(intent.getDataString());
        	/*
        	urlstore.addUrl(intent.toString());
        	PackageManager pm = getPackageManager();
        	intent.setComponent(null);
        	List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        	for (ResolveInfo ri: activities)
        	{
        		ActivityInfo ai = ri.activityInfo;
        		urlstore.addUrl(ai.packageName);
        		urlstore.addUrl(ai.toString());
        	}
        	*/
      
        }
        
        Cursor urls = urlstore.getUrlCursor();
        
        //setContentView(R.layout.main);
        int[] to = { R.id.tv1 };
        
        setListAdapter(new SimpleCursorAdapter(getApplicationContext(),
        									   R.layout.main, urls, 
        									   colFields,to));
        
        
    }
}