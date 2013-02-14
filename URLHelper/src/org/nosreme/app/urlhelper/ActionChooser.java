package org.nosreme.app.urlhelper;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ActionChooser extends Activity
{
	private class ActivityAdapter extends BaseAdapter {
		private Activity mActivity;
		private IntentResolver mResolver;
		private Context mContext;
		public ActivityAdapter(Activity activity, IntentResolver resolver)
		{
			mActivity = activity;
			mContext = activity.getApplicationContext();
			mResolver = resolver;
		}

		public int getCount() {
			return mResolver.count();
		}

		public Object getItem(int position) {
			return mResolver.getIntent(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				/* Our list never changes, so can always reuse views. */
				return convertView;
			}
			
			/* Otherwise, we need to create one. */
			LayoutInflater inflater = mActivity.getLayoutInflater();
			View v = inflater.inflate(R.layout.act_spinner, null);
			
			TextView tv = (TextView)v.findViewById(R.id.act_label);
			tv.setText(mResolver.getHumanName(position));
			return v;
		}
		
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();

		setContentView(R.layout.action_chooser);
		Spinner spinner = (Spinner)findViewById(R.id.spinner_openwith);
		IntentResolver resolver = new IntentResolver(getPackageManager(),
				                                     intent.getDataString());
		ActivityAdapter adapter = new ActivityAdapter(this, resolver);
		spinner.setAdapter(adapter);
		
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
