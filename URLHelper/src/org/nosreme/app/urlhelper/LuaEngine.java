package org.nosreme.app.urlhelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import android.content.res.Resources;

public class LuaEngine {
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
	public LuaEngine()
	{
		_G = createGlobals();
	}
	public String runStream(InputStream stream)
	{
		try {
		    LuaValue chunk = fullGlobals.compiler.load(stream, "", _G);
		    LuaValue lResult = chunk.call();
		
		    return lResult.toString();
		} catch (LuaError e) {
			return e.getMessage();
		} catch (IOException e) {
			return null;
		}	
	}
	public String runString(String script)
	{
		return runStream(new ByteArrayInputStream(script.getBytes()));
	}

}
