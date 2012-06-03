package org.nosreme.app.urlhelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class Main extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    final SharedPreferences prefs = getSharedPreferences("settings", 0);
	    setContentView(R.layout.main);
        final ToggleButton button = (ToggleButton) findViewById(R.id.toggleOffline);
        if (prefs.getBoolean("offline", true))
	    {
	        button.setChecked(true);
	    }
        else
        {
        	button.setChecked(false);
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ToggleButton button = (ToggleButton)v;
                prefs.edit().putBoolean("offline", button.isChecked()).commit();
            }
        });
	    
	}

}
