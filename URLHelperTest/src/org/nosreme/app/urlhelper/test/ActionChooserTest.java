package org.nosreme.app.urlhelper.test;

import java.lang.reflect.Field;

import org.nosreme.app.urlhelper.ActionChooser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class ActionChooserTest extends
		ActivityInstrumentationTestCase2<ActionChooser> {
	
	/* Simple class for returning the full result from an activity. */
	public class ActivityResult {
		public int code;
		public Intent data;
		public ActivityResult(int c, Intent d) {
			code = c;
			data = d;
		}
	}
	
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

	protected ActivityResult getResult(Activity activity)
	{
		/* Thanks to 
		 * http://stackoverflow.com/questions/5569830/get-result-from-an-activity-after-finish-in-an-android-unit-test
		 * for this way of finding the activity result.  There *must* be a
		 * better way... */
		try {
			Field f = Activity.class.getDeclaredField("mResultCode");
			f.setAccessible(true);
			int actualResultCode = (Integer)f.get(activity);
			f = Activity.class.getDeclaredField("mResultData");
			f.setAccessible(true);
			Intent realResult = (Intent)f.get(activity);
			return new ActivityResult(actualResultCode, realResult);
		} catch (NoSuchFieldException e) {
			assert false;
			return null;
		} catch (Exception e) {
			assert false;
			return null;
		}
		
	}
	
	public void testSimple() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
	    setActivityIntent(intent);
	    
	    ActionChooser activity = getActivity();
	    
	    final Button okButton = (Button)activity.findViewById(org.nosreme.app.urlhelper.R.id.choose_open);
	    
	    //okButton.requestFocus();
		activity.runOnUiThread(new Runnable() {
			public void run() {
				okButton.performClick();
			}
		});
	    
		getInstrumentation().waitForIdleSync();
		
		ActivityResult result = getResult(activity);
		assertEquals(result.code, Activity.RESULT_OK);	  
	}

	public void testVisible() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
	    setActivityIntent(intent);
	    
	    ActionChooser activity = getActivity();
	    
	    final CheckBox ruleCb = (CheckBox)activity.findViewById(org.nosreme.app.urlhelper.R.id.check_addrule);
	    View regex_label = activity.findViewById(org.nosreme.app.urlhelper.R.id.title_ruleregex);
	    View regex_entry = activity.findViewById(org.nosreme.app.urlhelper.R.id.multi_ruleregex);

	    /* Assume it starts unchecked */
	    assert !ruleCb.isChecked();
	    assertEquals(regex_label.getVisibility(), View.GONE);
	    assertEquals(regex_entry.getVisibility(), View.GONE);
	    
		activity.runOnUiThread(new Runnable() {
			public void run() {
				ruleCb.requestFocus();
			}
		});
   		getInstrumentation().waitForIdleSync();
   		sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
   		
   		/* They should now be visible */
	    assert ruleCb.isChecked();
	    assertEquals(regex_label.getVisibility(), View.VISIBLE);
	    assertEquals(regex_entry.getVisibility(), View.VISIBLE);

	    activity.runOnUiThread(new Runnable() {
			public void run() {
				ruleCb.requestFocus();
			}
		});
   		getInstrumentation().waitForIdleSync();
   		sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

   		/* And invisible again */
	    assert !ruleCb.isChecked();
	    assertEquals(regex_label.getVisibility(), View.GONE);
	    assertEquals(regex_entry.getVisibility(), View.GONE);
	}

	public void testCancel() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
	    setActivityIntent(intent);

	    ActionChooser activity = getActivity();
	    getInstrumentation().waitForIdleSync();

	    sendKeys(KeyEvent.KEYCODE_BACK);

	    getInstrumentation().waitForIdleSync();

		ActivityResult result = getResult(activity);
		assertEquals(result.code, Activity.RESULT_CANCELED);	  

	}
}
