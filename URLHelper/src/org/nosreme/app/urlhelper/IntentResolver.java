package org.nosreme.app.urlhelper;

import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/* Utility class for looking up lists of possible activities
 * with appropriate filtering.  */
public class IntentResolver {
	/* The passed in package manager. */
	private PackageManager pm;

	/* The array of ActivityInfo instances */
	private ResolveInfo[] activitylist;
	/* The size of activitylist */
	private int activityCount = 0;
	/* The number actually stored in the list */
	private int activitiesFound = 0;
	
    public IntentResolver(PackageManager pman, String urlString)
    {
    	pm = pman;
    	Uri uri = Uri.parse(urlString);
    	Intent intent = new Intent();
    	intent.setComponent(null);
    	
    	intent.setData(uri);
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	
    	/* Ask the system for possible activities which will handle this URL.
    	 * This is likely to include us, so we want to filter it out.
    	 */
    	List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
    	
    	activityCount = activities.size();
    	activitylist = new ResolveInfo[activityCount];

    	for (ResolveInfo ri: activities)
    	{
    		ActivityInfo ai = ri.activityInfo;
    		if (!ai.name.startsWith("org.nosreme.app.urlhelper"))
    		{
    			/*
    			Intent actIntent = new Intent();
    			actIntent.setClassName(ai.packageName, ai.name);
    			*/
    			activitylist[activitiesFound] = ri;
    			activitiesFound += 1;
    		}
    	}

    }
}
