local _G = _G

do
    local Toast = luajava.bindClass("android.widget.Toast")
    local PreferenceManager = luajava.bindClass("android.preference.PreferenceManager")
    local SharedPrefs = PreferenceManager:getDefaultSharedPreferences(ctx)
    
    function _G.toast(msg)
      t = Toast:makeText(ctx, msg, Toast.LENGTH_LONG)
      t:show()
    end
    
    _G.prefs = setmetatable({},
                            {
                               __index = function(t, key)
                                   -- Should the default be different?
                                   return SharedPrefs:getString(key, "")
                                end,
                                __newindex = function(t, k, v)
                           
                                   editor = SharedPrefs:edit()
                                   editor:putString(k, v)
                                   editor:commit()
                                end,
                            })
    
    local urlStoreTab = load("return " .. prefs["luaStore"] or "{}",
                             "Rule table",
                             "t", {})()
    urlStore = setmetatable({
      __index = urlStore,
      __newindex = function(t, k, v)
          urlStoreTab[k] = v
          prefs["luaStore"] = tostring(urlStoreTab)
      end,
      __tostring = function(t)
          local res = {}
          local function add(s)
              table.insert(res, s)
          end
          
          add("{")
          
          for i,v in ipairs(urlStoreTab) do
              add("{\"" .. v[1] .. "\", \"" .. v[2] .. "\"}")
          end
          
          add("}")
          return table.concat(res, ", ")
          
      end,
    }, urlStoreTab)
end
function _G.chosenAction(arg)
  toast("Chose action " .. tostring(arg))
end
x = prefs["launchimm"]
-- toast("Current mode: " .. x)

--toast(store:getUrl(1))

y = prefs["luatest"]
if string.len(y) > 0 then
  y = y + 1
else
  y = 1
end
y = tostring(y)
prefs["luatest"] = y
toast(y)


