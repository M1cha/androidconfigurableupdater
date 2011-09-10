package org.m1cha.android.configurableupdater.romtools;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class RomObject {

	private String version;
	private ArrayList<OptionObject> options = new ArrayList<OptionObject>();
	private String filename;
	private String changelog;
	private String coverFilename;
	private Bitmap coverBitmap;
	
	public RomObject(String updaterFile, String filename) throws JSONException, NullPointerException {
		/** parse JSON-String */
		JSONObject updater = new JSONObject(updaterFile);
		
		/** save version */
		this.version = updater.get("version").toString();
		
		/** save filename */
		this.filename = filename;
		
		/** save changelog */
		this.changelog = updater.get("changelog").toString();
		
		/** save cover-filename */
		this.coverFilename = updater.get("cover").toString();
		
		
		/** loop through options */
		JSONArray options = updater.getJSONArray("options");
		for(int j=0; j<options.length(); j++) {
			
			/** get option */
			JSONObject jsonOption = options.getJSONObject(j);
			
			/** create option-object */
			OptionObject option = new OptionObject();
			
			/** get and save option-values */
			option.setCategory(jsonOption.getString("category"));
			if(jsonOption.has("summary")) option.setSummary(jsonOption.getString("summary"));
			option.setDisplayName(jsonOption.getString("displayname"));
			option.setType(jsonOption.getString("type"));
			option.setDefaultValue(jsonOption.getInt("default"));

			/** save list if avaible */
			if(option.getType().equals("list")) {
				
				/** get lists */
				JSONArray jsonItems = jsonOption.getJSONArray("items");
				JSONArray jsonValues = jsonOption.getJSONArray("values");
				
				if(jsonItems.length()!=jsonValues.length()) {
					throw(new JSONException("'items' must have the same length like 'values'"));
				}
				
				/** convert lists to arrays */
				String[] items = new String[jsonItems.length()];
				String[] values = new String[jsonValues.length()];
				for(int k=0; k<jsonItems.length(); k++) {
					items[k] = jsonItems.getString(k);
					values[k] = jsonValues.getString(k);
				}
				
				/** save arrays in option-object */
				option.setItems(items);
				option.setValues(values);
			}
			else {
				option.setValue(jsonOption.getString("value"));
			}
			
			/** add option-object to arraylist */
			this.options.add(option);
		}
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public ArrayList<OptionObject> getOptions() {
		return this.options;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public String getChangelog() {
		return this.changelog;
	}
	
	public String getCoverFilename() {
		return this.coverFilename;
	}
	
	public Bitmap getCover() {
		return this.coverBitmap;
	}
	public void setCover(Bitmap b) {
		this.coverBitmap = b;
	}
}
