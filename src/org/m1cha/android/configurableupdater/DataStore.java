package org.m1cha.android.configurableupdater;

import org.json.JSONObject;
import org.m1cha.android.configurableupdater.romtools.RomObject;

public class DataStore {
	public static RomObject currentRom;
	public static JSONObject advancedSettings;
	public static boolean logging;
	public static boolean logcat;
	public static int checkCRCBeforeReboot;
}
