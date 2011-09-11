package org.m1cha.android.configurableupdater.romtools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.json.JSONException;
import org.m1cha.android.configurableupdater.Logger;
import org.m1cha.android.configurableupdater.R;
import org.m1cha.android.configurableupdater.Util;
import org.m1cha.android.configurableupdater.customexceptions.Long2IntegerException;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class RomList {
	
	private File sdcard;
	private ArrayList<RomObject> romList = new ArrayList<RomObject>();
	private Context context;
	private Pattern p = Pattern.compile("[-]+");
	
	public RomList(Context context, String path) {
		/** save context */
		this.context = context;
		
		/** open directory */
		this.sdcard = Environment.getExternalStoragePublicDirectory(path);
		
		/** check if we can read */
		if(!sdcard.canRead() || !sdcard.isDirectory()) {
			Util.alert(this.context, context.getString(R.string.lang_error_sdcard));
			return;
		}
		
		/** loop through files in folder */
		String[] fileList = sdcard.list();
		for(int i=0; i<fileList.length; i++) {
			String fileName = fileList[i];
			if(fileName.length()<4) {
				continue;
			}
			String ext = fileName.substring(fileName.length()-4, fileName.length());
			
			if(ext.equals(".zip")) {

				try {
					/** open zip-file */
					ZipFile zipFile = new ZipFile(this.sdcard.getAbsolutePath()+"/"+fileName);
					Logger.debug("opened zip-File");
					
					/** get infofile */
					ZipEntry entry = zipFile.getEntry("version");
					
					/** check if version-file exists */
					if(entry==null) {
						Logger.debug("File '"+fileName+"' is not a valid Updater-archive.");
						continue;
					}
					
					/** get content of updater-file */
					String updaterFile = Util.getEntryContentAsString(zipFile, entry);
					Logger.debug("got updater-file");
					
					/** parse romObject from json-File */
					RomObject romObject = new RomObject(updaterFile, fileName);
					Logger.debug("parsed jsonFile to RomObject");
					
					/** convert and save cover */
					if(zipFile.getEntry(romObject.getCoverFilename())!=null) {
						ZipEntry coverEntry = zipFile.getEntry(romObject.getCoverFilename());
						InputStream is = zipFile.getInputStream(coverEntry);
						romObject.setCover(BitmapFactory.decodeStream(is));
					}
					Logger.debug("tried to load cover");
					
					/** close zip-file */
					zipFile.close();
					
					/** add rom to list */
					this.romList.add(romObject);
				} 
				
				/** exceptions */
				catch (IOException e) {
					Logger.debug("File '"+fileName+"' could not be opened as ZIP-archive.", e);
				} catch (Long2IntegerException e) {
					Logger.debug("File '"+fileName+"' has a version-file which is too big.", e);
				} catch (JSONException e) {
					Logger.debug("File '"+fileName+"' has an error in it's version-file.", e);
				} catch (NullPointerException e) {
					Logger.debug("File '"+fileName+"' has an error in it's version-file.", e);
				}
			}
		}
	}
	
	public String[] getRomNames() {
		String[] names = new String[this.romList.size()];
		
		for(int i=0; i<this.romList.size(); i++) {
			
			/** filename */
			String fileName = this.romList.get(i).getFilename();
			
			/** remove extension */
			fileName = fileName.substring(0, fileName.length()-4);
			
			/** split at '-' */
			String[] romName = p.split(fileName, 2);
			
			names[i] = romName[0]+"("+this.romList.get(i).getVersion()+")";
		}
		
		return names;
	}
	
	public void applyRomNameOptions() {
		
	}
	
	public RomObject getRom(int i) {
		return this.romList.get(i);
	}
}
