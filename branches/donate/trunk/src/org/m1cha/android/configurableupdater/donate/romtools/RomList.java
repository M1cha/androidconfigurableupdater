package org.m1cha.android.configurableupdater.donate.romtools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.json.JSONException;
import org.m1cha.android.configurableupdater.donate.Logger;
import org.m1cha.android.configurableupdater.donate.Util;
import org.m1cha.android.configurableupdater.donate.customexceptions.Long2IntegerException;

import android.content.Context;
import android.graphics.BitmapFactory;

public class RomList {
	
	private File sdcard;
	private ArrayList<RomObject> romList = new ArrayList<RomObject>();
	
	public static final int ERROR_EXTERNAL_STORAGE_CANNOT_READ = 100;
	public static final int ERROR_DIRECTORY_NOT_FOUND = 101;
	public static final int ERROR_NOT_A_DIRECTORY = 102;
	public static final int ERROR_DIRECTORY_CANNOT_READ = 103;
	public static final int NO_ERROR = -1;
	
	private int lastestError = NO_ERROR;
	
	public int getLastestError() {
		return this.lastestError;
	}
	
	public RomList(Context context, String path) {
		
		/** check if external storage is readable */
		if(!Util.getExternalStorage("").canRead()) {
			this.lastestError = ERROR_EXTERNAL_STORAGE_CANNOT_READ;
			return;
		}
		
		/** open directory */
		this.sdcard = Util.getExternalStorage(path);
		
		/** check if path exists */
		if(!sdcard.exists()) {
			this.lastestError = ERROR_DIRECTORY_NOT_FOUND;
			return;
		}
		
		/** check if it's a directory */
		if(!sdcard.isDirectory()) {
			this.lastestError = ERROR_NOT_A_DIRECTORY;
			return;
		}
		
		/** check if directory is readable */
		if(!sdcard.canRead()) {
			this.lastestError = ERROR_DIRECTORY_CANNOT_READ;
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
					Logger.debug("found: "+fileName);
					
					/** get infofile */
					ZipEntry entry = zipFile.getEntry("version");
					
					/** check if version-file exists */
					if(entry==null) {
						Logger.debug("File '"+fileName+"' is not a valid Updater-archive.");
						continue;
					}
					
					/** get content of updater-file */
					String updaterFile = Util.getEntryContentAsString(zipFile, entry);
					Logger.debug("loaded version-file");
					
					/** parse romObject from json-File */
					Logger.debug("\r\n============================== START PARSING ROMOBJECT ==============================");
					RomObject romObject = new RomObject(updaterFile, fileName);
					Logger.debug("============================ FINISHED PARSING ROMOBJECT =============================\r\n");
					
					
					
					/** convert and save cover */
					if(zipFile.getEntry(romObject.getCoverFilename())!=null) {
						ZipEntry coverEntry = zipFile.getEntry(romObject.getCoverFilename());
						InputStream is = zipFile.getInputStream(coverEntry);
						romObject.setCover(BitmapFactory.decodeStream(is));
						
						Logger.debug("loaded cover");
					}
					else {
						Logger.debug("cover not found!");
					}
					
					/** create and save file-object */
					romObject.setFile(new File(this.sdcard, fileName));
					Logger.debug("SUCCESSFULLY PARSED ROMFILE");
					
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
					Logger.debug("File '"+fileName+"' has an error in it's version-file:JSON", e);
				} catch (NullPointerException e) {
					Logger.debug("File '"+fileName+"' has an error in it's version-file:NullPointer", e);
				} catch (ArrayIndexOutOfBoundsException e) {
					Logger.debug("File '"+fileName+"' has an error in it's version-file:noKernelVersion", e);
				}
			}
		}
	}
	
	public String[] getRomNames() {
		String[] names = new String[this.romList.size()];
		
		for(int i=0; i<this.romList.size(); i++) {
			names[i] = this.romList.get(i).getRomName()+"("+this.romList.get(i).getVersion()+")";
		}
		
		return names;
	}
	
	public RomObject getRom(int i) {
		return this.romList.get(i);
	}
}
