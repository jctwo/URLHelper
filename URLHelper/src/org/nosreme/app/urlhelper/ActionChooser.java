package org.nosreme.app.urlhelper;
import android.app.*;
import android.os.Bundle;
import android.view.*;


public class ActionChooser extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.action_chooser);
	}
	
	public void buttonCancel(View view) {
	    finish();
	}
}
