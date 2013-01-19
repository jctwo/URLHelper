package org.nosreme.app.urlhelper;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

public class LuaTest {
	LuaValue _G;
	
	public LuaTest()
	{
		_G = JsePlatform.standardGlobals();
	}
	public String runString(String script)
	{
		LuaValue chunk = _G.get("load").call(script);
		LuaValue lResult = chunk.call();
		
		return lResult.toString();
	}
}
