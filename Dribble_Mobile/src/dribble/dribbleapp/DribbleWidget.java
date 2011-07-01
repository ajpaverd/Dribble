package dribble.dribbleapp;

import java.util.ArrayList;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import dribble.common.DribSubject;

// Creates a dribble widget for the homescreen
public class DribbleWidget extends AppWidgetProvider 
{
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == null) 
		{
			context.startService(new Intent(context, DribbleService.class));
		}
		else 
		{
			super.onReceive(context, intent);		
		}
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{ 
		context.startService(new Intent(context, DribbleService.class));
	}

public static class DribbleService extends IntentService 
{ 
	public DribbleService() 
	{
		super("DribbleWidget$DribbleService"); 
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{ 
		ComponentName me= new ComponentName(this, DribbleWidget.class); 
		AppWidgetManager mgr=AppWidgetManager.getInstance(this);
		mgr.updateAppWidget(me, buildUpdate(this));
	}
	private RemoteViews buildUpdate(Context context) 
	{
		RemoteViews updateViews=new RemoteViews(context.getPackageName(),R.layout.widget);
		Intent i=new Intent(this, DribbleWidget.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,0);
		updateViews.setOnClickPendingIntent(R.id.dribble_icon, pi);
		int results = DribbleSharedPrefs.getNumDribTopics(getApplicationContext());
		ArrayList<DribSubject> dribTopAr= DribCom.getTopics(results);
		DribSubject ds = ((DribSubject)(dribTopAr.toArray())[0]);
		updateViews.setTextViewText(R.id.drib_widget, "Top Subject: " + ds.getName());
		return updateViews;
	}

}

}
