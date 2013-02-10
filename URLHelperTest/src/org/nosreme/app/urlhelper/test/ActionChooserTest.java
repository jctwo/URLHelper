package org.nosreme.app.urlhelper.test;

import java.lang.reflect.Field;

import org.nosreme.app.urlhelper.ActionChooser;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;

public class ActionChooserTest extends
		ActivityInstrumentationTestCase2<ActionChooser> {
	
	public ActionChooserTest() {
		super("org.nosreme.app.urlhelper", ActionChooser.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    super.setUp();

	    setActivityInitialTouchMode(false);

	}

	public void testPreConditions() {
	    /* Nothing yet to test */
	}
	
	public void testSimple() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
	    setActivityIntent(intent);
	    
	    ActionChooser activity = getActivity();
	    
	    /* Thanks to 
             * http://stackoverflow.com/questions/5569830/get-result-from-an-activity-after-finish-in-an-android-unit-test
	     * for this way of finding the activity result.  There *must* be a
	     * better way... */
	    final Button okButton = (Button)activity.findViewById(org.nosreme.app.urlhelper.R.id.choose_open);
	    
	    //okButton.requestFocus();
		activity.runOnUiThread(new Runnable() {
			public void run() {
				okButton.performClick();
			}
		});
	    
		getInstrumentation().waitForIdleSync();
	  
		try {
			Field f = Activity.class.getDeclaredField("mResultCode");
			f.setAccessible(true);
			int actualResultCode = (Integer)f.get(getActivity());
			assertEquals(actualResultCode, Activity.RESULT_OK);
			f = Activity.class.getDeclaredField("mResultData");
			f.setAccessible(true);
			Intent realResult = (Intent)f.get(getActivity());
		} catch (NoSuchFieldException e) {
			assert false;
		} catch (Exception e) {
			assert false;
		}
	}
}
