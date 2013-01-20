package org.nosreme.app.urlhelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import android.util.Log;

public class LuaTest {
	private Globals fullGlobals;
	private LuaValue _G;
	
	private static void copyField(LuaValue dst, LuaValue src, String key)
	{
		dst.set(key, src.get(key));
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
	    fullGlobals = JsePlatform.standardGlobals();
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
	public LuaTest()
	{
		_G = createGlobals();
	}
	public String runString(String script)
	{
		try {
		    LuaValue chunk = fullGlobals.compiler.load(new ByteArrayInputStream(script.getBytes()), "", _G);
		    LuaValue lResult = chunk.call();
		
		    return lResult.toString();
		} catch (LuaError e) {
			return e.getMessage();
		} catch (IOException e) {
			return null;
		}
	}
}
