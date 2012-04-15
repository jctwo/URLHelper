package org.nosreme.app.urlhelper;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import org.nosreme.app.urlhelper.UrlStore;

public class URLHelperActivity extends ListActivity {
	private final String[] colFields = { "URL" };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        UrlStore urlstore = new UrlStore(getApplicationContext());
        Cursor urls = urlstore.getUrlCursor();
        
        //setContentView(R.layout.main);
        int[] to = { R.id.tv1 };
        
        setListAdapter(new SimpleCursorAdapter(getApplicationContext(),
        									   R.layout.main, urls, 
        									   colFields,to));
        
    }
}