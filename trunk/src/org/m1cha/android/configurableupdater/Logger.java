package org.m1cha.android.configurableupdater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class Logger {
	public static final String TAG = "UPDATER";
	public static final boolean DEBUG = true;
	public static final boolean PRINT_STACKTRACE = true;
	
	public static void debug(String msg) {
		
		logToFile(msg, null);
		if(DEBUG) Log.d(TAG, msg);
	}
	
	public static void debug(String msg, Exception e) {
		
		logToFile(msg, e);
		
		if(DEBUG) {
			Log.d(TAG, msg);
			
			if(PRINT_STACKTRACE) {
				e.printStackTrace();
			}
		}
	}
	
	private static void logToFile(String msg, Exception e) {
		if(DataStore.logging) {
			
			try {
				File logFile = Util.getExternalStorage("androidconfigurableupdater.log");
				logFile.createNewFile();
				
				if(logFile.canWrite()) {
					BufferedWriter os = new BufferedWriter(new FileWriter(logFile, true));
					os.write((msg+"\r\n"));
					if(e!=null) os.write(e.toString()+"\r\n");
					os.close();
				}
			} catch (IOException e1) {
			}
		}
	}
}