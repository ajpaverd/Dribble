package com.dribble.dribbleapp;

import com.dribble.dribbleapp.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DribbleSharedPrefs {
    public final static String PREFS_NAME = "dribble_prefs";
 
    // Taken out 20/08/2011 - Chad
//    public static boolean getUseGPS(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
//        return prefs.getBoolean(context.getString(R.string.pref_key_use_gps),
//                false);
//    }
    
    public static int getNumDribTopics(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return Integer.parseInt(prefs.getString(context.getString(R.string.pref_key_num_dribs), "5"));
    }
}
    
