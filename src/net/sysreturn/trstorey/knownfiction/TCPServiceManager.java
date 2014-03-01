package net.sysreturn.trstorey.knownfiction;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class TCPServiceManager extends BroadcastReceiver {

		public static final String TAG = "TCP";
		public static final String PREFS = "unkn_own-prefsFile";
		private SharedPreferences mPrefs;
		@Override
		public void onReceive(Context context, Intent intent) {
			// Make sure we are getting the right intent
			if( "net.sysreturn.trstorey.knownfiction.servicebroadcast".equals(intent.getAction())) {
				Log.w(TAG, "received intent");
				mPrefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_MULTI_PROCESS);
		        boolean mUpdatesRequested = mPrefs.getBoolean("updatesSetting", false);
		        if(mUpdatesRequested){
		        	Log.e(TAG, "Starting service");
					ComponentName comp = new ComponentName(context.getPackageName(), TCPService.class.getName());
					ComponentName service = context.startService(new Intent().setComponent(comp));
					
					if (null == service){
						// something really wrong here
						Log.e(TAG, "Could not start service " + comp.toString());
					}
		        }
				
			} else {
				Log.e(TAG, "Received unexpected intent " + intent.toString());   
			}
		}

}
