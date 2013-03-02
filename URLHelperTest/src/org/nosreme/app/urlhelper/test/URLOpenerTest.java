package org.nosreme.app.urlhelper.test;
import java.lang.reflect.InvocationTargetException;

import org.nosreme.app.urlhelper.ActionChooser;
import org.nosreme.app.urlhelper.URLOpenActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.RenamingDelegatingContext;

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
		/* Don't use the real application files! */
		Context context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
		setActivityContext(context);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		Editor editor = prefs.edit();
		editor.putString("launchimm", "always");
		editor.commit();

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"));

		Intent fakeResultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"));
		fakeResultIntent.setClassName("org.example.apps", "exampleClass");

		startActivity(intent, null, null);
		
        getInstrumentation().waitForIdleSync();
        
        /* By now the activity should have tried to kick off an ActionChooser
         * to find out what to do.  Check this is the case.
         */
        Intent sent = getStartedActivityIntent();
        assertNotNull(sent);
        assertEquals(sent.getDataString(), "http://www.example.com/");
        ComponentName comp = sent.getComponent();
        assertEquals(comp.flattenToString(), "org.nosreme.app.urlhelper/org.nosreme.app.urlhelper.ActionChooser");
        
        int requestId = getStartedActivityRequest();
        
        /* The next bit is kind of gross.  We need to pretend that ActionChooser
         * has returned a result.  The Android test framework doesn't seem to
         * help us here, and we can't call onActivityResult() directly as it's
         * protected.  So it's a bit gross, but reflection lets us call it
         * anyway.
         */
        URLOpenActivity activity = getActivity();        
        java.lang.reflect.Method method;

        try {
          method = activity.getClass().getDeclaredMethod("onActivityResult", int.class, int.class, Intent.class);
          method.setAccessible(true);
      	  method.invoke(activity, requestId, ActionChooser.RESULT_OPEN, fakeResultIntent);
        } catch (SecurityException e) {
        	fail("Security exception");
        } catch (NoSuchMethodException e) {
        	fail("NoSuchMethodException");
        } catch (IllegalArgumentException e) {
        	fail("IllegalArgumentException");
        } catch (IllegalAccessException e) {
        	fail("IllegalAccessException");
        } catch (InvocationTargetException e) {
        	fail("InvolationTargetException");
        }
        /* And check for idle etc. */
        getInstrumentation().waitForIdleSync();

        /* This is the simple OPEN case, where it should just launch the
         * intent passed back from the (dummy) ActionChooser.
         */
        sent = getStartedActivityIntent();
        assertSame(sent, fakeResultIntent);
	}
}
