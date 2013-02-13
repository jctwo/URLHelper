package org.nosreme.app.urlhelper;
import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;


public class ActionChooser extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.action_chooser);
		
		/* Set various visibilities */
		updateVisibleWidgets();
	}
	
	private void updateVisibleWidgets() {
		/* Hide the regex if we're not adding a rule */
		CheckBox addRuleCb = (CheckBox)findViewById(R.id.check_addrule);
		View ruleLabel = findViewById(R.id.title_ruleregex);
		View ruleEntry = findViewById(R.id.multi_ruleregex);
		int vis = addRuleCb.isChecked()? View.VISIBLE : View.GONE;
		ruleLabel.setVisibility(vis);
		ruleEntry.setVisibility(vis);
		
		/* Enable/disable activity spinner */
		RadioButton openRad = (RadioButton)findViewById(R.id.radio_openwith);
		Spinner activitySpinner = (Spinner)findViewById(R.id.spinner_openwith);
		activitySpinner.setEnabled(openRad.isChecked());
	}
	
	/* Called when the "OK" button is pressed */
	public void buttonOk(View v)
	{
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
	/* Called when one of the action radio buttons is clicked. */
	public void radioClicked(View v)
	{
        updateVisibleWidgets();				
	}
	/* Called when the "add rule" checkbox is clicked. */
	public void ruleCheckClicked(View v)
	{
         updateVisibleWidgets();		
	}
}
