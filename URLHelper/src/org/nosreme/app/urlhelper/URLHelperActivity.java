package org.nosreme.app.urlhelper;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.ToggleButton;

import org.nosreme.app.urlhelper.UrlStore;

public class URLHelperActivity extends ListActivity {
	private final String[] colFields = { "url", "seen" };
	
	private final int REQ_CHOOSE_INTENT = 0;

	/* Launch a URL using the configured browser. */
	private void launchUrl(String urlString)
	{
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
    	Parcelable[] activitylist = new Parcelable[activityCount-1];
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
    			activitylist[activitiesFound++] = actIntent;
    		}
    	}
    	
    	/* If more than one found, create a popup to ask. */
    	if (activitiesFound > 1)
    	{
    		Intent chooserIntent = new Intent();
    		chooserIntent.setAction(Intent.ACTION_CHOOSER);
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
    		startActivityForResult(chooserIntent, REQ_CHOOSE_INTENT);    		
    	} else {
    		/* Only one, so use it directly. */
    	    intent.setClassName(pkg, name);
        	startActivity(intent);
    	}

	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        UrlStore urlstore = new UrlStore(getApplicationContext());

        Intent intent = getIntent();
        if (intent.getAction().equals(android.content.Intent.ACTION_VIEW))
        {
        	urlstore.addUrl(intent.getDataString());
        }
        
        /* Check whether we're in offline mode. */
        SharedPreferences prefs = getSharedPreferences("settings", 0);
        boolean offlineSetting = prefs.getBoolean("offline", true);
 	    setContentView(R.layout.main);
	    
        ToggleButton button = (ToggleButton) findViewById(R.id.toggleOffline);
        button.setChecked(offlineSetting);
        button.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
		    	SharedPreferences prefs = getSharedPreferences("settings", 0);
		    	Log.v("URLHandler", "on click value: " + isChecked);
		        prefs.edit().putBoolean("offline", isChecked).commit();
		    	Log.v("URLHandler", "committed (count = " + prefs.getInt("count", -1));				
			}
        	
        });
        
    	/* If online, simply relaunch it. */
    	if (intent.getAction().equals(android.content.Intent.ACTION_VIEW) && !offlineSetting)
    	{
    	    launchUrl(intent.getDataString());
    	}
    	else
    	{
    		showList(urlstore);
    	}
        	
    }
    
	private void showList(UrlStore urlstore) {
		Cursor urls = urlstore.getUrlCursor();

		int[] to = { R.id.tv1 };
        
        setListAdapter(new SimpleCursorAdapter(getApplicationContext(),
        									   R.layout.urllist, urls, 
        									   colFields,to));
        
        
        ListView lv = getListView();
        /* Thanks to tranbinh.bino@gmail.com in the thread at:
         * http://groups.google.com/group/android-developers/browse_thread/thread/14ba131c3ebc49eb
         * for this snippet. 
         */
        lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener 
        		(){ 
        		                //@Override 
        		                
        		                public boolean onItemLongClick(AdapterView<?> av, View v, int	pos, long id) { 
        		                        onLongListItemClick(v,pos,id); 
        		                        return true; 
        		        } 
        		});
	}
    
    private void onLongListItemClick(View v, int pos, long id)
    {
   	    AlertDialog dlg = new AlertDialog.Builder(this).create();
    	
    	dlg.setMessage("List item long click" + Long.toString(id));
    	dlg.show();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        UrlStore urlstore = new UrlStore(getApplicationContext());
    	
        String urlString = urlstore.getUrl(id);

        launchUrl(urlString);
    }
    
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	if (requestCode == REQ_CHOOSE_INTENT)
    	{
    		Log.i("URLHandler", "Got activity result");
    		if (resultCode == RESULT_OK) {
    			startActivity(data);
    		}
    	}
    }
}