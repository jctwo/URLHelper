local _G = _G

local URL = luajava.bindClass("java.net.URL")
local Intent = luajava.bindClass("android.content.Intent")
local Uri = luajava.bindClass("android.net.Uri")

do
    local Toast = luajava.bindClass("android.widget.Toast")
    local PreferenceManager = luajava.bindClass("android.preference.PreferenceManager")
    local SharedPrefs = PreferenceManager:getDefaultSharedPreferences(ctx)
    
    function _G.toast(msg)
      t = Toast:makeText(ctx, msg, Toast.LENGTH_SHORT)
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
function _G.chosenAction(activity, action, intent, addrule, regex)
    if action == "open" then
      toast("starting intent")
      activity:startActivity(intent)
  else if action == "expand" then
      url = intent:getDataString()
      toast("expanding " .. url)
      runAsync(coroutine.create(function()
          local u = luajava.new(URL, url)
          local conn = u:openConnection()
          conn:setRequestMethod("HEAD")
          conn:setInstanceFollowRedirects(false)
	  -- See http://code.google.com/p/android/issues/detail?id=16227
	  -- and http://code.google.com/p/android/issues/detail?id=24672
	  -- for why the Accept-Encoding is required to disable gzip
	  conn:setRequestProperty("Accept-Encoding", "identity")
	  local resp = conn:getResponseCode()
	  if resp == 301 or resp == 302 then
	      local headers = conn:getHeaderFields()
	      local locs = headers:get("location")
	      if locs:size() == 1 then
	        return locs:get(0)
	      end
	  end

      end), function (...)
         local t = table.pack(...)
         toast("resume "..tostring(t.n)..","..tostring(t[1])..","..tostring(t[2]))
      end, url)
  else
  toast("Chose action " .. tostring(action) .. ","..tostring(intent)..","..tostring(addrule)..","..tostring(regex))

  end end
end
x = prefs["launchimm"]
-- toast("Current mode: " .. x)

--toast(store:getUrl(1))
if false then
y = prefs["luatest"]
if string.len(y) > 0 then
  y = y + 1
else
  y = 1
end
y = tostring(y)
prefs["luatest"] = y
toast(y)
end

local function ask_default_browser(activity, arg)
  toast("Ask for default browser")
  if nil then return end
  local intent = luajava.new(Intent)
  local example = luajava.new(Intent)
  

    if nil then return end
    uri = Uri:parse("http://www.example.com/")
    --err, rest = pcall(example.setData, example, uri)
    --toast("err="..tostring(err)..",rest="..tostring(rest))
    if nil then return end
  example:setData(uri)
  if nil then return end
  intent:setAction(Intent.ACTION_PICK_ACTIVITY)
  
  intent:putExtra(Intent.EXTRA_INTENT, example)
  if nil then
  activity:startActivityForResult(intent, 4)
  else
    toast("££")
    err, rest = pcall(activity.startActivityForResult, activity, intent, 4)
    toast("err="..tostring(err)..",rest="..tostring(rest))
  end
end

local widgets = {
    chooseact = ask_default_browser,
}
function _G.testact(arg, activity)
  toast("testact "..tostring(arg))
  handler = widgets[arg]
  if handler then handler(activity, arg) end
end

function _G.onActivityResult(code, result, data)
  toast("onActivityResult("..tostring(code)..","..
                          tostring(result)..","..
                          tostring(data)..")")
end
