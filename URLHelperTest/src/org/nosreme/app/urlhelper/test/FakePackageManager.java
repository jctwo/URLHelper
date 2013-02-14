package org.nosreme.app.urlhelper.test;
import android.content.pm.*;
import android.test.mock.*;
import java.util.*;
import android.content.*;

/* Fake package manager with known contents */
public class FakePackageManager extends MockPackageManager {
    private ResolveInfo makeInfo(String humanName,
				 String pkg,
				 String compName)
    {
	ResolveInfo ri;
	ActivityInfo ai;

	ri = new ResolveInfo();
	ai = new ActivityInfo();
	ai.name = compName;
	ai.packageName = pkg;
	ri.nonLocalizedLabel = humanName;
	ri.activityInfo = ai;

	return ri;
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
	ArrayList<ResolveInfo> l = new ArrayList<ResolveInfo>();

	l.add(makeInfo("Browser", "com.android.browser", "com.android.browser.BrowserActivity"));
	l.add(makeInfo("URLHelper", "org.nosreme.app.urlhelper", "org.nosreme.app.urlhelper.URLHelper"));
	l.add(makeInfo("Firefox", "org.mozilla.fennec", "org.mozilla.fennec.Firefox"));

	return l;
    }		
}

