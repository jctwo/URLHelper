package org.nosreme.app.urlhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		UrlStore urlstore = new UrlStore(getApplicationContext());
		Intent intent = getIntent();
		long id = intent.getLongExtra("urlid", -1);
		
		String url = urlstore.getUrl(id);
		String orig_url = urlstore.getOrigUrl(id);
		
		setContentView(R.layout.details);
		
		TextView urlview = (TextView)findViewById(R.id.details_url);
		urlview.setText(url);
		
		urlview = (TextView)findViewById(R.id.details_orig_url); 
		urlview.setText("Original URL: " + orig_url);
	}

}
