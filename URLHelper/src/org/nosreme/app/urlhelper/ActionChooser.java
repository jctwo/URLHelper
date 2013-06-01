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
import android.widget.*;
import java.io.*;

public class ActionChooser extends Activity
{

    //public static final String INTENT_RESULT = "org.nosreme.intent.result";

    public static final int RESULT_OPEN = RESULT_FIRST_USER;

    public static final int RESULT_EXPAND = RESULT_OPEN + 1;

    /* Extra intent field for "add rule" flag */
    public static final String EXTRA_INTENT = "_ac_intent";
    public static final String EXTRA_ADDRULE = "_ac_addrule";
    public static final String EXTRA_REGEX = "_ac_regex";
    
    private LuaEngine lua;
    private class ActivityAdapter extends BaseAdapter
    {
	private Activity mActivity;
	private IntentResolver mResolver;
	private Context mContext;
	public ActivityAdapter(Activity activity, IntentResolver resolver)
	{
	    mActivity = activity;
	    mContext = activity.getApplicationContext();
	    mResolver = resolver;
	}

	public int getCount()
	{
	    return mResolver.count();
	}

	public Object getItem(int position)
	{
	    return mResolver.getIntent(position);
	}

	public long getItemId(int position)
	{
	    return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
	    if (convertView != null)
	    {
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
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	Intent intent = getIntent();
	String urlString = intent.getDataString();

	setContentView(R.layout.action_chooser);
	Spinner spinner = (Spinner)findViewById(R.id.spinner_openwith);
	IntentResolver resolver = new IntentResolver(getPackageManager(),
						     urlString);
	ActivityAdapter adapter = new ActivityAdapter(this, resolver);
	spinner.setAdapter(adapter);

	TextView tv = (TextView)findViewById(R.id.text_url);
	tv.setText(urlString);

	/* Set various visibilities */
	updateVisibleWidgets();

	{	
	    Context ctx = getApplicationContext();

	    lua = new LuaEngine(ctx);

	    try
	    {
		lua.runStreamPrivileged(ctx.getResources().getAssets().open("lua/startup.lua"));
	    }
	    catch (IOException e)
	    {
		String result = "failed";
		Toast t2 = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
		t2.show();
	    }				

	}
    }

    private void updateVisibleWidgets()
    {
	/* Hide the regex if we're not adding a rule */
	CheckBox addRuleCb = (CheckBox)findViewById(R.id.check_addrule);
	View ruleLabel = findViewById(R.id.title_ruleregex);
	View ruleEntry = findViewById(R.id.multi_ruleregex);
	int vis = addRuleCb.isChecked() ? View.VISIBLE : View.GONE;
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
	RadioButton rbOpen = (RadioButton)findViewById(R.id.radio_openwith);
	RadioButton rbExpand = (RadioButton)findViewById(R.id.radio_expand);
	RadioButton rbAdv = (RadioButton)findViewById(R.id.radio_advanced);
	CheckBox cbAddRule = (CheckBox)findViewById(R.id.check_addrule);
	TextView tvRegex = (TextView)findViewById(R.id.multi_ruleregex);

	Intent resultIntent = new Intent();
	int result;
	
	String action;
	if (rbOpen.isChecked()) {
	    action = "open";
	} else if (rbExpand.isChecked()) {
	    action = "expand";
	} else if (rbAdv.isChecked()) {
	    action = "advanced";
	} else {
	    return;
	}
	
        Spinner activitySpinner = (Spinner)findViewById(R.id.spinner_openwith);
	lua.call("chosenAction", this, action,
	         activitySpinner.getSelectedItem(),
		 cbAddRule.isChecked(), tvRegex.getText());
	
//
//	if (rbOpen.isChecked())
//	{
//	    Spinner activitySpinner = (Spinner)findViewById(R.id.spinner_openwith);
//	    startActivity((Intent)activitySpinner.getSelectedItem());
//	}
//	else if (rbExpand.isChecked())
//	{
//	    result = RESULT_EXPAND;
//	}
//	else
//	{
//	    result = RESULT_CANCELED;
//	}
//
//	if (cbAddRule.isChecked())
//	{
//	    resultIntent.putExtra(EXTRA_ADDRULE, true);
//	    resultIntent.putExtra(EXTRA_REGEX, tvRegex.getText());
//	}
//
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
