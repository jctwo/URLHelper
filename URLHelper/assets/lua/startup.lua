local _G = _G

Toast = luajava.bindClass("android.widget.Toast")
t = Toast:makeText(ctx, "Test toast", Toast.LENGTH_LONG)
t:show()
