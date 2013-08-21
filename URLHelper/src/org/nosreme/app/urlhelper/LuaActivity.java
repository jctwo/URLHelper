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
    private int pendingResult = 0;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	/*Toast t = Toast.makeText(getApplicationContext(), "lua activity", Toast.LENGTH_SHORT);
	t.show();
	*/
	
	Intent intent = getIntent();
	String urlString = intent.getDataString();

	{	
	    Context ctx = getApplicationContext();

	    lua = new LuaEngine(ctx);

	    try
	    {
		lua.runStreamPrivileged(ctx.getResources().getAssets().open("lua/startup.lua"));
	        lua.call("testact", intent.getData().getHost(), this);
            }
	    catch (IOException e)
	    {
		String result = "failed";
		Toast t2 = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
		t2.show();
	    }		

	}
        if (pendingResult == 0) {
	    finish();
	}
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options)
    {
	pendingResult += 1;
	super.startActivityForResult(intent, requestCode, options);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	pendingResult -= 1;
	    lua.call("onActivityResult", requestCode, resultCode, data);
	
        if (pendingResult == 0)
	{
	    finish();
	}
    }
    
}
