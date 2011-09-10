package org.m1cha.android.configurableupdater;

import android.util.Log;

public class Logger {
	private static String TAG = "UPDATER";
	
	public static void debug(String msg) {
		Log.d(TAG, msg);
	}
}