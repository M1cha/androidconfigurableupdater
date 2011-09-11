package org.m1cha.android.configurableupdater.romtools;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.m1cha.android.configurableupdater.Logger;

import android.graphics.Bitmap;

public class RomObject {

	private String version;
	private ArrayList<OptionObject> options = new ArrayList<OptionObject>();
	private String filename;
	private String romName;
	private String changelog;
	private String coverFilename;
	private Bitmap coverBitmap;
	private File fileobject;
	private Pattern p = Pattern.compile("[-]+");
	
	public RomObject(String updaterFile, String filename) throws JSONException, NullPointerException {
		
		/** get options defined in filename */
		String fileNameWithOutExtension = filename.substring(0, filename.length()-4);
		String[] romNameOptions = p.split(fileNameWithOutExtension);
	
		/** parse JSON-String */
		JSONObject updater = new JSONObject(updaterFile);
		Logger.debug("[RomObject] parsed json-string");
		
		/** save version */
		this.version = updater.get("version").toString();
		Logger.debug("[RomObject] got version");
		
		/** save filename */
		this.filename = filename;
		Logger.debug("[RomObject] got filename");
		
		/** make clean romName */
		this.romName = p.split(filename.substring(0, filename.length()-4), 2)[0];
		
		/** save changelog */
		this.changelog = updater.get("changelog").toString();
		Logger.debug("[RomObject] got changelog");
		
		/** save cover-filename */
		this.coverFilename = updater.get("cover").toString();
		Logger.debug("[RomObject] got cover-filename");
		
		
		/** loop through options */
		JSONArray options = updater.getJSONArray("options");
		Logger.debug("[RomObject] got options-array");
		for(int j=0; j<options.length(); j++) {
			
			/** get option */
			JSONObject jsonOption = options.getJSONObject(j);
			
			/** create option-object */
			OptionObject option = new OptionObject();
			
			/** get and save option-values */
			option.setCategory(jsonOption.getString("category"));
			Logger.debug("[RomObject:"+j+"] got category");
			if(jsonOption.has("summary")) option.setSummary(jsonOption.getString("summary"));
			Logger.debug("[RomObject:"+j+"] tried to get summary");
			option.setDisplayName(jsonOption.getString("displayname"));
			Logger.debug("[RomObject:"+j+"] got displayname");
			option.setType(jsonOption.getString("type"));
			Logger.debug("[RomObject:"+j+"] got type");
			option.setDefaultValue(jsonOption.getInt("default"));
			Logger.debug("[RomObject:"+j+"] got defaultValue");

			/** save list if avaible */
			if(option.getType().equals("list")) {
				
				/** get lists */
				JSONArray jsonItems = jsonOption.getJSONArray("items");
				Logger.debug("[RomObject:"+j+"|List] got items");
				JSONArray jsonValues = jsonOption.getJSONArray("values");
				Logger.debug("[RomObject:"+j+"|List] got values");
				
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
				Logger.debug("[RomObject:"+j+"|List] converted jsonarrays to normal arrays");
				
				/** save arrays in option-object */
				option.setItems(items);
				Logger.debug("[RomObject:"+j+"|List] saved items");
				option.setValues(values);
				Logger.debug("[RomObject:"+j+"|List] saved values");
				
				/** overwrite json-default with filename-options */
				for(int i=1; i<romNameOptions.length; i++) {
					boolean found=false;
					for(int z=0; z<values.length; z++) {
						if(values[z].equals(romNameOptions[i])) {
							option.setDefaultValue(z);
							found=true;
							break;
						}
					}
					if(found) break;
				}
			}
			else {
				option.setValue(jsonOption.getString("value"));
				Logger.debug("[RomObject:"+j+"|Checkbox] got value");
				
				/** overwrite json-default with filename-options */
				for(int i=1; i<romNameOptions.length; i++) {
					if(romNameOptions[i].equals(option.getValue())) {
						option.setDefaultValue(1);
						break;
					}
					else {
						option.setDefaultValue(0);
					}
				}
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
	
	public File getFile() {
		return this.fileobject;
	}
	public void setFile(File f) {
		this.fileobject = f;
	}
	
	public String getRomName() {
		return this.romName;
	}
}
