package org.nosreme.app.urlhelper.test;
import org.nosreme.app.urlhelper.ActionChooser;
import org.nosreme.app.urlhelper.URLOpenActivity;
import org.nosreme.app.urlhelper.test.ActionChooserIsolatedTest.FakeContext;
import org.nosreme.app.urlhelper.test.IntentHelperTest.ActivityResult;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

public class URLOpenerTest extends IntentHelperTest<URLOpenActivity>

{
	public URLOpenerTest() {
		super(URLOpenActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	public void testSimple()
	{
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"));

		Intent fakeResultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"));
		fakeResultIntent.setClassName("org.example.apps", "exampleClass");

		/* Fake the chooser result */
		Instrumentation.ActivityResult fakeResult = new Instrumentation.ActivityResult(ActionChooser.RESULT_OPEN, intent);
		Instrumentation.ActivityMonitor monitor = new Instrumentation.ActivityMonitor("ActionChooser", fakeResult, true);
		getInstrumentation().addMonitor(monitor);
		IntentFilter filterAll = new IntentFilter(Intent.ACTION_VIEW);
		filterAll.addDataScheme("http");
		Instrumentation.ActivityMonitor monitorAll = new Instrumentation.ActivityMonitor(filterAll, null, true);
		getInstrumentation().addMonitor(monitorAll);

		startActivity(intent, null, null);

        /* Check that our monitors have been hit */
		assertEquals(1, monitor.getHits());
		
		assertEquals(1, monitorAll.getHits());
		Activity lastSeen = monitorAll.getLastActivity();
		
		assertEquals(lastSeen.getPackageName(), "org.example.apps");
		assertEquals(lastSeen.getComponentName(), "exampleClass");
	}
}
