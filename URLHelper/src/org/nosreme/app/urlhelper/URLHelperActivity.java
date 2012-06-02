package org.nosreme.app.urlhelper;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.nosreme.app.urlhelper.UrlStore;

public class URLHelperActivity extends ListActivity {
	private final String[] colFields = { "url", "seen" };
	
	private final int REQ_CHOOSE_INTENT = 0;
	
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
 
    	PackageManager pm = getPackageManager();
    	Intent intent = new Intent();
    	intent.setComponent(null);
        UrlStore urlstore = new UrlStore(getApplicationContext());
    	
        String urlString = urlstore.getUrl(id);
    	Uri uri = Uri.parse(urlString);
    	
    	intent.setData(uri);
    	intent.setAction(android.content.Intent.ACTION_VIEW);
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
    			if (pkg == null)
    			{
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
    		
    		startActivityForResult(chooserIntent, REQ_CHOOSE_INTENT);    		
    	} else {
    		/* Only one, so use it directly. */
    	    intent.setClassName(pkg, name);
        	startActivity(intent);
    	}

//    	AlertDialog dlg = new AlertDialog.Builder(this).create();
    	
//    	dlg.setMessage(msg);
//    	dlg.show();
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