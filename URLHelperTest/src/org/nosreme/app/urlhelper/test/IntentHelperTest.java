package org.nosreme.app.urlhelper.test;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

/* Add a method for getting the result from an activity. */
public abstract class IntentHelperTest<T extends Activity> extends
		ActivityUnitTestCase<T> {

	/* Simple class for returning the full result from an activity. */
	public class ActivityResult {
		public int code;
		public Intent data;
		public ActivityResult(int c, Intent d) {
			code = c;
			data = d;
		}
	}
	
	public IntentHelperTest(Class activityClass) {
		super(activityClass);
	}

	protected ActivityResult getResult(Activity activity) {
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

}