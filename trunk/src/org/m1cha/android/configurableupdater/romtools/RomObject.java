package org.m1cha.android.configurableupdater.romtools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.m1cha.android.configurableupdater.Logger;
import org.m1cha.android.configurableupdater.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.KeyEvent;

public class RomObject {

	private String version;
	private ArrayList<OptionObject> options = new ArrayList<OptionObject>();
	private String filename;
	private String romName;
	private String changelog;
	private String coverFilename;
	private Bitmap coverBitmap;
	private File fileobject;
	private String kernelVersion;
	private Pattern p = Pattern.compile("[-]+");
	
	public RomObject(String updaterFile, String filename) throws JSONException, NullPointerException {
		
		/** prepare filename */
		String fileNameWithOutExtension = filename.substring(0, filename.length()-4);
		
		/** get kernel-version */
		String[] kernelVersionSplit = p.split(fileNameWithOutExtension, 3);
		this.kernelVersion = kernelVersionSplit[1];
		for(int i=0; i<kernelVersionSplit.length; i++) {
			Logger.debug("kernelVersionSplit["+i+"] : "+kernelVersionSplit[i]);
		}
		
		/** get options defined in filename */
		String[] romNameOptions = p.split(kernelVersionSplit[2]);
		for(int i=0; i<romNameOptions.length; i++) {
			Logger.debug("romNameOptions["+i+"] : "+romNameOptions[i]);
		}
	
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
			Logger.debug("[RomObject]["+j+"] got category");
			
			if(jsonOption.has("summary")) {
				option.setSummary(jsonOption.getString("summary"));
				Logger.debug("[RomObject]["+j+"] got summary");
			}
			else {
				Logger.debug("[RomObject]["+j+"] no summary found!");
			}
			
			option.setDisplayName(jsonOption.getString("displayname"));
			Logger.debug("[RomObject]["+j+"] got displayname");
			option.setType(jsonOption.getString("type"));
			Logger.debug("[RomObject]["+j+"] got type");
			option.setDefaultValue(jsonOption.getInt("default"));
			Logger.debug("[RomObject]["+j+"] got defaultValue");

			/** save list if avaible */
			if(option.getType().equals("list")) {
				
				/** get lists */
				JSONArray jsonItems = jsonOption.getJSONArray("items");
				Logger.debug("[RomObject]["+j+"|List] got items");
				JSONArray jsonValues = jsonOption.getJSONArray("values");
				Logger.debug("[RomObject]["+j+"|List] got values");
				
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
				Logger.debug("[RomObject]["+j+"|List] converted jsonArrays to normal arrays");
				
				/** save arrays in option-object */
				option.setItems(items);
				Logger.debug("[RomObject]["+j+"|List] saved items");
				option.setValues(values);
				Logger.debug("[RomObject]["+j+"|List] saved values");
				
				/** overwrite json-default with filename-options */
				for(int i=0; i<romNameOptions.length; i++) {
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
				Logger.debug("[RomObject]["+j+"|List] loaded defaultValues");
			}
			else {
				option.setValue(jsonOption.getString("value"));
				Logger.debug("[RomObject]["+j+"|Checkbox] got value");
				
				/** overwrite json-default with filename-options */
				for(int i=0; i<romNameOptions.length; i++) {
					if(romNameOptions[i].equals(option.getValue())) {
						option.setDefaultValue(1);
						break;
					}
					else {
						option.setDefaultValue(0);
					}
				}
				Logger.debug("[RomObject]["+j+"|Checkbox] loaded defaultValues");
			}
			
			/** add option-object to arraylist */
			this.options.add(option);
			Logger.debug("[RomObject]["+j+"] successfully added parsed option to list");
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
	public String getKernelVersion() {
		return this.kernelVersion;
	}
	
	public boolean isCurrentRomFile(File f) {
		if(f.getName().equals(this.getFile().getName())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static final int RENAME_ERROR_ALREADY_EXISTS = 100;
	public static final int RENAME_ERROR_NOT_WRITABLE = 101;
	public static final int RENAME_ERROR_UNKNOWN = 102;
	public static final int RENAME_SUCCESS = -1;
	public int renameRomFile(File f) {
		
		/** check if file already exists */
		if(f.exists()) {
			return RENAME_ERROR_ALREADY_EXISTS;
		}
		
		/** check if we can write */
		if(!this.getFile().canWrite()) {
			return RENAME_ERROR_NOT_WRITABLE;
		}
		
		/** rename file */
		if(this.getFile().renameTo(f)) {
			
			/** save new filename */
			this.setFile(f);
		}
		else {
			return RENAME_ERROR_UNKNOWN;
		}
		
		return RENAME_SUCCESS;
	}
	
	/**
	 * Task for asynchronous CRC-check
	 */
	private class CheckCRC_Task extends AsyncTask<String, Integer, String> {
		
		private ZipFile zipFile;
		
		@Override
		protected void onPostExecute(String result) {
			
			mProgressDialog.dismiss();
			crcCheckFinishedCallback.onFinish(Boolean.parseBoolean(result));
			
			super.onPostExecute(result);
		}
		
    	@Override
    	protected void onProgressUpdate(Integer... args) {
    		
    		/** update progress-dialog */
    		mProgressDialog.setProgress(args[0]);
    		
    		super.onProgressUpdate(args);
    	}
    	
        @Override
        protected String doInBackground(String... params) {
        	
            try {
				/** open zipFile */
				zipFile = new ZipFile(getFile());
				
				/** get entries */
    			Enumeration<? extends ZipEntry> entries = zipFile.entries();
    			
    			/** calculate percentage-factor */
    			float factor = 100/(float)(zipFile.size());
    			
    			/** enumerate over entries */
    			int countProcessedEntries = 0;
    			while(entries.hasMoreElements()) {
    				
    				/** count up +1 */
    				countProcessedEntries++;
    				
    				/** get current entry */
    				ZipEntry entry = entries.nextElement();
    				
    				/** continue if current entry is a directory */
    				if(entry.isDirectory()) {continue;}
    				
    				/** initialise crc-check */
    				InputStream fis = zipFile.getInputStream(entry);
    				CRC32 crc = new CRC32();
    				CheckedInputStream cis = new CheckedInputStream(fis, crc);
    				
    				/** calculate crc */
    				byte[] buffer = new byte[4086];
    				while(cis.read(buffer)>=0){}
    				long checksum = cis.getChecksum().getValue();
    				
    				/** close streams */
    				cis.close();
    				fis.close();
    				
    				/** check if crc matches */
    				if(checksum!=entry.getCrc()) {
    					Logger.debug("Checksum-Error in File '"+entry.getName()+"'!");
    					
    					/** close zipFile */
    		        	zipFile.close();
    		        	
    					return Boolean.toString(false);
    				}
    				
    				/** publish progress */
    				publishProgress(Math.round(factor*countProcessedEntries));
    				Logger.debug("CRC-Progess: "+Math.round(factor*countProcessedEntries)); 
    			}
    			
    			/** close zipFile */
	        	zipFile.close();
    			
    			/** finish with success-message */
	        	return Boolean.toString(true);
				
			} catch (ZipException e) {
				Logger.debug("CheckCRC_Task", e);
				return Boolean.toString(false);
			} catch (IOException e) {
				Logger.debug("CheckCRC_Task", e);
				return Boolean.toString(false);
			}
        }        
    }
	
	
	private ProgressDialog mProgressDialog;
	private onCRCCheckFinished crcCheckFinishedCallback;
	private CheckCRC_Task crcTask;
	public void checkCRC(Context c, onCRCCheckFinished callback) {
 
		/** save callback */
		this.crcCheckFinishedCallback = callback;
		
		/** create dialog */
		mProgressDialog = new ProgressDialog(c);
        mProgressDialog.setMessage(c.getString(R.string.lang_crcCheck_message));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        
        /** add cancel-button */
        mProgressDialog.setButton(c.getString(R.string.lang_alert_buttonCancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				crcTask.cancel(true);
			}
		});
        
        /** FIX: Don't dismiss dialog when pressing the search-button */
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            	
                if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
                    return true;
                }
                return false;
            }
        });
        
        /** show dialog */
        mProgressDialog.show();
        
        /** execute Task */
        crcTask = new CheckCRC_Task();
        crcTask.execute();
	}
	
	public static class onCRCCheckFinished {
		public void onFinish(boolean success) {}
	}
}
