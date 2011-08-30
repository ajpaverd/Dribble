package com.dribble.dribbleapp;

import java.util.ArrayList;
import java.util.TimerTask;

import com.dribble.common.DribSubject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RefreshTimerTask extends TimerTask {

	private static final String TAG= "RefreshTimerTask";
	private TelephonyManager telephonyManager;
	
	private DribCom dribCom;
	
	public RefreshTimerTask(Context context){
				
				dribCom = new DribCom(context);
				//telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	}

	@Override
	public void run() {
		//Log.i(TAG,"Current Network Data Type is: "+telephonyManager.getNetworkType());
		//if(telephonyManager.getNetworkType()!=0)
		//{
			try{
				Log.i(TAG,"Retrieving Topics in thread");
				if(SubjectActivity.dribTopAr!=null){
					Log.i(TAG,"Refresfing drib topic array");
				}
				SubjectActivity.dribTopAr = dribCom.getTopics(GpsListener.currentLocation);

			}catch(NullPointerException npe)
			{
				Log.e(TAG, "The message is: "+npe.getMessage());
				//If no topics, displays default "no topics available" message
			}
		}
	}
//}


