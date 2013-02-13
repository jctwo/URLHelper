package org.nosreme.app.urlhelper;

import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

/* Utility class for looking up lists of possible activities
 * with appropriate filtering.  */
public class IntentResolver {
	/* The passed in package manager. */
	private PackageManager pm;

	/* The array of ActivityInfo instances */
	private ResolveInfo[] activityList;
	/* The size of activityList */
	private int activityCount = 0;
	/* The number actually stored in the list */
	private int activitiesFound = 0;
	/* The initial URL */
	private Uri uri;
	
    public IntentResolver(PackageManager pman, String urlString)
    {
    	pm = pman;
    	uri = Uri.parse(urlString);
    	Intent intent = new Intent();
    	intent.setComponent(null);
    	
    	intent.setData(uri);
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	
    	/* Ask the system for possible activities which will handle this URL.
    	 * This is likely to include us, so we want to filter it out.
    	 */
    	List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
    	
    	activityCount = activities.size();
    	activityList = new ResolveInfo[activityCount];

    	for (ResolveInfo ri: activities)
    	{
    		ActivityInfo ai = ri.activityInfo;
    		if (!ai.name.startsWith("org.nosreme.app.urlhelper"))
    		{
    			/*
    			Intent actIntent = new Intent();
    			actIntent.setClassName(ai.packageName, ai.name);
    			*/
    			activityList[activitiesFound] = ri;
    			activitiesFound += 1;
    		}
    	}

    }
    
    /* Return the number of items in the list. */
    public int count()
    {
    	return activitiesFound;
    }
    
    /* Return the activity readable name */
    public String getHumanName(int index)
    {
    	assert index < activitiesFound;
    	
    	return activityList[index].loadLabel(pm).toString();
    }
    
    /* Return an explicit Intent to launch the URL with a chosen
     * item. */
    public Intent getIntent(int index)
    {
    	assert index < activitiesFound;
    	ResolveInfo ri = activityList[index];
    	
    	Intent intent = new Intent();
    	intent.setData(uri);
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	Log.v("TestResolve", "package" + ri.activityInfo.packageName);
    	Log.v("TestResolve", "name" + ri.activityInfo.name);
    	intent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);

    	return intent;
    }
}
