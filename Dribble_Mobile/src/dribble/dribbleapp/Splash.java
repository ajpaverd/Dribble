// Authors: Dribble
// Date: 24 April 2010
// Class: Splash

package dribble.dribbleapp;

import dribble.dribbleapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

	
	public class Splash extends Activity {
		
		private static final String TAG = "Splash";
	     
		private final int SPLASH_DISPLAY_LENGHT = 100;

	     @Override
	     public void onCreate(Bundle icicle) {
	          super.onCreate(icicle);
	          Log.i(TAG, "Dribble Application Started");
	          setContentView(R.layout.splash);
	          
	          /* New Handler to start the Menu-Activity
	           * and close this Splash-Screen after some seconds.*/
	          new Handler().postDelayed(new Runnable(){
	               public void run() {
	                    /* Create an Intent that will start the Menu-Activity. */
	                    Intent mainIntent = new Intent(Splash.this,DribbleTabs.class);
	                    Log.i(TAG, "Start Dribble Activity");
	                    Splash.this.startActivity(mainIntent);
	                   
	                    Splash.this.finish();
	                    Log.i(TAG, "Splash Activity Finished");
	               }
	          }, SPLASH_DISPLAY_LENGHT);
	     }
	}
