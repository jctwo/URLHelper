package org.nosreme.app.urlexpander;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class URLExpanderActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

    	/* If online, simply relaunch it. */
    	if (intent.getAction().equals(android.content.Intent.ACTION_VIEW))
    	{
//    	    launchUrl(intent.getDataString());
    	}

    }


}