package org.nosreme.app.urlhelper.test;


import org.nosreme.app.urlhelper.ActionChooser;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ActionChooserIsolatedTest extends
		IntentHelperTest<ActionChooser> {
	
	/* Wrapper so we an supply our own context */
	public class FakeContext extends ContextWrapper {
		private Application mApp;
		
		public FakeContext(Context context, Application app) {
			super(context);
			mApp = app;
		}
		
		/* Provide a fake package manager instance */
		@Override
		public PackageManager getPackageManager() {
			return new FakePackageManager();
		}
		
		@Override
	    public Context getApplicationContext() {
			return mApp;
		}
		

	}
	
	public ActionChooserIsolatedTest() {
		super(ActionChooser.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    super.setUp();

	}

	public void testPreConditions() {
	    /* Nothing yet to test */
	}

	public void testSimple() {
		Context context = new FakeContext(this.getInstrumentation().getTargetContext(),
				(Application)this.getInstrumentation().getTargetContext().getApplicationContext());
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);

		setActivityContext(context);

		ActionChooser activity = startActivity(intent, null, null);
	
	        /* This seems necessary to make sure the UI had updated (eg the TextView)
		 */
	    activity.runOnUiThread(new Runnable() {
		    public void run() {
			
		    }
		});
		
	        getInstrumentation().waitForIdleSync();
		
		TextView tv = (TextView)activity.findViewById(org.nosreme.app.urlhelper.R.id.text_url);
	    assertEquals(tv.getText(), "http://www.example.com/");
	    
	    final Button okButton = (Button)activity.findViewById(org.nosreme.app.urlhelper.R.id.choose_open);
	    
		activity.runOnUiThread(new Runnable() {
			public void run() {
				okButton.performClick();
			}
		});
	    
		getInstrumentation().waitForIdleSync();
		
		ActivityResult result = getResult(activity);
		assertEquals(result.code, ActionChooser.RESULT_OPEN);	  
	       
		assertEquals(result.data.getComponent().flattenToString(), "com.android.browser/com.android.browser.BrowserActivity");
	}
	
    public void testExpand() {
	Context context = new FakeContext(this.getInstrumentation().getTargetContext(),
					  (Application)this.getInstrumentation().getTargetContext().getApplicationContext());
	final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);

	setActivityContext(context);

	ActionChooser activity = startActivity(intent, null, null);

	final Button okButton = (Button)activity.findViewById(org.nosreme.app.urlhelper.R.id.choose_open);
	final RadioButton expandRad = (RadioButton)activity.findViewById(org.nosreme.app.urlhelper.R.id.radio_expand);
	
	doClick(activity, expandRad);
	doClick(activity, okButton);

	ActivityResult result = getResult(activity);
	assertEquals(result.code, ActionChooser.RESULT_EXPAND);	  

    }

    private void doClick(ActionChooser activity, Button but)
    {
	final Button theBut = but;
	activity.runOnUiThread(new Runnable() {
		public void run()
		{
		    theBut.performClick();
		}
	    });
	getInstrumentation().waitForIdleSync();
    }

    
	public void testVisible() throws Throwable {
		//Application app = new FakeApplication();
		Context context = new FakeContext(this.getInstrumentation().getTargetContext(),
				                          (Application)this.getInstrumentation().getTargetContext().getApplicationContext());
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);

		setActivityContext(context);
		//setApplication(app);
		
		runTestOnUiThread(new Runnable() {
		        public void run() {
		            startActivity(intent, null, null);
		        }
		});
	    ActionChooser activity = getActivity(); //startActivity(intent, null, null);
	    
	    final CheckBox ruleCb = (CheckBox)activity.findViewById(org.nosreme.app.urlhelper.R.id.check_addrule);
	    View regex_label = activity.findViewById(org.nosreme.app.urlhelper.R.id.title_ruleregex);
	    View regex_entry = activity.findViewById(org.nosreme.app.urlhelper.R.id.multi_ruleregex);

	    /* Assume it starts unchecked */
	    assertFalse(ruleCb.isChecked());
	    assertEquals(regex_label.getVisibility(), View.GONE);
	    assertEquals(regex_entry.getVisibility(), View.GONE);
	    
		activity.runOnUiThread(new Runnable() {
			public void run() {
				ruleCb.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();
   		
   		/* They should now be visible */
	    assertTrue(ruleCb.isChecked());
	    assertEquals(regex_label.getVisibility(), View.VISIBLE);
	    assertEquals(regex_entry.getVisibility(), View.VISIBLE);

	    activity.runOnUiThread(new Runnable() {
			public void run() {
				ruleCb.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();

   		/* And invisible again */
	    assertFalse(ruleCb.isChecked());
	    assertEquals(regex_label.getVisibility(), View.GONE);
	    assertEquals(regex_entry.getVisibility(), View.GONE);
	    
	    /* Now check that the "Open with..." spinner is enabled at the right
	     * times. */
	    final Spinner spinner = (Spinner)activity.findViewById(org.nosreme.app.urlhelper.R.id.spinner_openwith);
	    final RadioButton openRad = (RadioButton)activity.findViewById(org.nosreme.app.urlhelper.R.id.radio_openwith);
	    final RadioButton expandRad = (RadioButton)activity.findViewById(org.nosreme.app.urlhelper.R.id.radio_expand);
	    assertNotNull(openRad);
	    assertNotNull(spinner);
	    /* Should start with this one selected */
	    assertTrue(openRad.isChecked());
	    assertFalse(expandRad.isChecked());
	    assertTrue(spinner.isEnabled());
	    
	    /* Select another radio button, and check the spinner is disabled. */
	    activity.runOnUiThread(new Runnable() {
			public void run() {
				expandRad.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();
   		
   		assertFalse(openRad.isChecked());
	    assertTrue(expandRad.isChecked());
	    assertFalse(spinner.isEnabled());
	    
	    /* Now check that the spinner has suitable entries */
	    assertEquals(2, spinner.getCount());	    

	    /* Go back to Open With. */
	    activity.runOnUiThread(new Runnable() {
			public void run() {
				openRad.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();

   		/* Attempt to select the second option */
   		spinner.setSelection(1);
	    final Button okButton = (Button)activity.findViewById(org.nosreme.app.urlhelper.R.id.choose_open);
	    
		activity.runOnUiThread(new Runnable() {
			public void run() {
				okButton.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();

		ActivityResult result = getResult(activity);
		assertEquals(result.code, ActionChooser.RESULT_OPEN);
		assertEquals(result.data.getComponent().flattenToString(), "org.mozilla.fennec/org.mozilla.fennec.Firefox");
	}

	public void testCancel() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
	    ActionChooser activity = startActivity(intent, null, null);
	    getInstrumentation().waitForIdleSync();

	    sendKeys(KeyEvent.KEYCODE_BACK);

	    getInstrumentation().waitForIdleSync();

		ActivityResult result = getResult(activity);
		assertEquals(result.code, Activity.RESULT_CANCELED);	  

	}
}
