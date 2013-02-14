package org.nosreme.app.urlhelper.test;

import org.nosreme.app.urlhelper.IntentResolver;

import android.content.ComponentName;
import android.content.Intent;
import android.test.AndroidTestCase;
import android.util.Log;

public class IntentResolverTest extends AndroidTestCase {

	public void testResolver() {
		IntentResolver resolver = new IntentResolver(new FakePackageManager(), "http://www.example.org/");
		
		assertNotNull(resolver);
		
		/* We should at least have a web browser */
		int num = resolver.count();
		assert num >= 1;
		
		boolean browserFound = false;
		/* Check that the default browser is found.  May need to check for eg Chrome if
		 * Browser isn't always there. */
		for (int i=0; i<num; ++i)
		{
			Log.v("TestResolve", "Name is: <" + resolver.getHumanName(i) + ">");
			if (resolver.getHumanName(i).equals("Browser"))
			{
				browserFound = true;
			}
			
			Intent intent = resolver.getIntent(i);
			ComponentName compName = intent.getComponent();
			assertNotNull(compName);
			
			Log.v("TestResolve", "package is: " + compName.getPackageName());
			assertFalse(compName.getPackageName().equals("org.nosreme.app.urlhelper"));
		}
		assertTrue(browserFound);
	}
}
