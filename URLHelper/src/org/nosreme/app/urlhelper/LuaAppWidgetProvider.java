package org.nosreme.app.urlhelper;
import android.appwidget.*;
import android.content.*;
import android.app.*;
import android.widget.*;
import android.net.Uri;

public class LuaAppWidgetProvider extends AppWidgetProvider
{
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
			 int[] appWidgetIds)
    {
	final int N = appWidgetIds.length;
	// Perform this loop procedure for each App Widget that belongs to this provider
	for (int i=0; i<N; i++) {
	    int appWidgetId = appWidgetIds[i];
	    // Create an Intent to launch ExampleActivity
	    Intent intent = new Intent(context, LuaActivity.class);
	    intent.setData(Uri.parse("lua://testact"));
	    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	    // Get the layout for the App Widget and attach an on-click listener
	    // to the button
	    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.url_controlwidget);
	    views.setOnClickPendingIntent(R.id.luabutton, pendingIntent);
	 
	    intent.setData(Uri.parse("lua://widget"));
	    pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	    
	    views.setOnClickPendingIntent(R.id.luabutton2, pendingIntent);
	    // Tell the AppWidgetManager to perform an update on the current app widget
	    appWidgetManager.updateAppWidget(appWidgetId, views);
	}
    }
}
