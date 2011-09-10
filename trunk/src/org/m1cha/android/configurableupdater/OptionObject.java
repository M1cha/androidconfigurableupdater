package org.m1cha.android.configurableupdater;

public class OptionObject {
	private String category;
	private String displayname;
	private int defaultValue;
	private String type;
	private String[] items;
	private String[] values;
	private String value;
	
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
	
	/** VALUES */
	public String getValue() {
		return this.value;
	}
	public void setValue(String s) {
		this.value = s;
	}
}
