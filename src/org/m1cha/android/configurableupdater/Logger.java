package org.m1cha.android.configurableupdater;

import android.util.Log;

public class Logger {
	public static final String TAG = "UPDATER";
	public static final boolean DEBUG = true;
	public static final boolean PRINT_STACKTRACE = true;
	
	public static void debug(String msg) {
		if(DEBUG) Log.d(TAG, msg);
	}
	public static void debug(String msg, Exception e) {
		if(DEBUG) {
			Log.d(TAG, msg);
			
			if(PRINT_STACKTRACE) {
				e.printStackTrace();
			}
		}
	}
}