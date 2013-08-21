package org.nosreme.app.urlhelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

public class LuaEngine
{
    private Globals fullGlobals;
    private LuaValue _G;
    private Context ctx;

    private static void copyField(LuaValue dst, LuaValue src, String key)
    {
	dst.set(key, src.get(key));
    }
    private class assetSearcher extends VarArgFunction
    {
	private AssetManager assets;
	public assetSearcher(AssetManager assetman)
	{
	    assets = assetman;
	}
	public Varargs invoke(Varargs args)
	{
	    if (args.checkstring(1) == null)
	    {
		return NIL;
	    }
	    String modname = args.tojstring(1);

	    try
	    {
		InputStream stream = assets.open("lua/" + modname + ".lua");
		return loadStreamRaw(stream, _G);
	    }
	    catch (IOException e)
	    {
		return NIL;
	    }

	}

    }
    protected final String[] wantedGlobals = {
	"unpack",
	"rawget",
	"tostring",
	"math",
	"bit32",
	"rawlen",
	"ipairs",
	"tonumber",
	//"coroutine",
	"string",
	"next",
	"getmetatable",
	//"xpcall",
	//"dofile",
	"select",
	//"os",
	"pcall",
	//"debug",
	"rawequal", 
	//"loadstring",
	"pairs",
	"table",
	//"module", "require",
	"_VERSION", "type",
	//"load",
	"assert",
	"error",
	"setmetatable",
	"rawset"};
    public LuaValue createGlobals()
    {
	fullGlobals = JsePlatform.debugGlobals();

	fullGlobals.package_.searchers.set(2, new assetSearcher(ctx.getAssets()));

	// Create the privileged reference to ctx
	fullGlobals.set("ctx", CoerceJavaToLua.coerce(ctx));
	fullGlobals.set("UrlStore", CoerceJavaToLua.coerce(UrlStore.class));
	fullGlobals.set("runAsync", new LuaRunAsync());
	//LuaValue g = new LuaTable();
	LuaValue g = new LuaTable();

	for (String name: wantedGlobals)
	{
	    copyField(g, fullGlobals, name);
	}
	g.set("_ENV", g);
	g.set("_G", g);

	return g;
    }
    public LuaEngine(Context context)
    {
	ctx = context;
	_G = createGlobals();
    }
    public String runStreamPrivileged(InputStream stream)
    {
	return runStreamInt(stream, fullGlobals);
    }
    public String runStream(InputStream stream)
    {
	return runStreamInt(stream, _G);
    }
    public String call(String func, Object... vals)
    {
	LuaValue f = fullGlobals.get(func);
	LuaValue[] lvs = new LuaValue[vals.length];
	for (int i=0; i < vals.length; ++i)
	{
	    lvs[i] = CoerceJavaToLua.coerce(vals[i]);
	}

	Varargs result = f.invoke(LuaValue.varargsOf(lvs));
	return result.tojstring();
    }
    private LuaValue loadStreamRaw(InputStream stream, LuaValue globals)
    {
	try
	{
	    LuaValue chunk = fullGlobals.compiler.load(stream, "", globals);
	    return chunk;
	}
	catch (IOException e)
	{
	    return null;
	}
    }
    private String runStreamInt(InputStream stream, LuaValue globals)
    {
	try
	{
	    LuaValue chunk = fullGlobals.compiler.load(stream, "", globals);
	    LuaValue lResult = chunk.call();

	    return lResult.toString();
	}
	catch (LuaError e)
	{
	    return e.getMessage();
	}
	catch (IOException e)
	{
	    return null;
	}	
    }
    public String runString(String script)
    {
	return runStream(new ByteArrayInputStream(script.getBytes()));
    }
    class LuaRunAsync extends TwoArgFunction
    {

	public LuaValue call(LuaValue co, LuaValue post)
	{
	    try {
	        new LuaAsyncTask().execute(co, post);
		return LuaValue.NIL;
	    } catch (LuaError e) {
		return CoerceJavaToLua.coerce(e.getMessage());
 	    }
	    
	}

	
    }
    class LuaAsyncTask extends AsyncTask<LuaValue, Void, Varargs>
    {

	LuaThread bgTask;
	LuaValue postAction;
        protected Varargs doInBackground(LuaValue... co)
	{
	    bgTask = co[0].checkthread();
	    postAction = co[1];
	    return bgTask.resume(LuaValue.varargsOf(co, 2, co.length-2));
        }

        protected void onPostExecute(Varargs result)
	{
	    postAction.invoke(result);
        }
    }
}
