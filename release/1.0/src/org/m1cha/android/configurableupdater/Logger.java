package org.m1cha.android.configurableupdater;

import android.util.Log;

public class Logger {
	private static String TAG = "UPDATER";
	private static boolean DEBUG = false;
	private static boolean PRINT_STACKTRACE = false;
	
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