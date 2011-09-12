package org.m1cha.android.configurableupdater.romtools;

import android.preference.Preference;

public class OptionObject {
	private String category;
	private String displayname;
	private String summary;
	private int defaultValue;
	private String type;
	private String[] items;
	private String[] values;
	private String value;
	private Preference preference;
	
	/** CATEGORY */
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String s) {
		this.category = s;
	}
	
	/** DISPLAYNAME */
	public String getDisplayName() {
		return this.displayname;
	}
	public void setDisplayName(String s) {
		this.displayname = s;
	}
	
	/** SUMMARY */
	public String getSummary() {
		return this.summary;
	}
	public void setSummary(String s) {
		this.summary = s;
	}
	
	/** TYPE */
	public String getType() {
		return this.type;
	}
	public void setType(String s) {
		this.type = s;
	}
	
	/** DEFAULTVALUE */
	public int getDefaultValue() {
		return this.defaultValue;
	}
	public void setDefaultValue(int i) {
		this.defaultValue = i;
	}
	
	/** ITEMS */
	public String[] getItems() {
		return this.items;
	}
	public void setItems(String[] sr) {
		this.items = sr;
	}
	
	/** VALUES */
	public String[] getValues() {
		return this.values;
	}
	public void setValues(String[] sr) {
		this.values = sr;
	}
	
	/** VALUE */
	public String getValue() {
		return this.value;
	}
	public void setValue(String s) {
		this.value = s;
	}
	
	/** PREFERENCE */
	public Preference getPreference() {
		return this.preference;
	}
	public void setPreference(Preference p) {
		this.preference = p;
	}
}
