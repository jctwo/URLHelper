package org.nosreme.app.urlhelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import java.io.IOException;
import android.widget.Toast;

public class LuaActivity extends Activity
{

    private LuaEngine lua;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	Intent intent = getIntent();
	String urlString = intent.getDataString();

	{	
	    Context ctx = getApplicationContext();

	    lua = new LuaEngine(ctx);

	    try
	    {
		lua.runStreamPrivileged(ctx.getResources().getAssets().open("lua/startup.lua"));
	        lua.call("testact", intent.getData().getHost());
            }
	    catch (IOException e)
	    {
		String result = "failed";
		Toast t2 = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
		t2.show();
	    }				

	}
        finish();
    }
    
}
