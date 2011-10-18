package org.m1cha.android.configurableupdater.activities;

import java.io.File;
import java.util.ArrayList;
import org.m1cha.android.configurableupdater.DataStore;
import org.m1cha.android.configurableupdater.R;
import org.m1cha.android.configurableupdater.Util;
import org.m1cha.android.configurableupdater.romtools.OptionObject;
import org.m1cha.android.configurableupdater.romtools.RomObject;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OptionSelectionActivity extends PreferenceActivity {

	public Button buttonSave, buttonReboot = null;
	private RomObject currentRom;
	private ArrayList<OptionObject> options;
	private MainActivity ma;
	private PreferenceScreen main;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Util.menuHandler(this, item);
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		/** hide bottom-layout */
		ma.getBottomLayout().setVisibility(LinearLayout.GONE);
	}
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    /** get parent */
		this.ma = (MainActivity)getParent();
	    
	    /** set title */
	    setTitle(R.string.lang_optionSelection_title);
	    
	    /** load preference-xml */
	    addPreferencesFromResource(R.xml.pref_option_selection);
	    main = getPreferenceScreen();
	    
	    /** add buttons */
	    this.addButtons();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		/** show bottom-layout */
		ma.getBottomLayout().setVisibility(LinearLayout.VISIBLE);
		
	    /** remove current preferences */
	    main.removeAll();
		
		/** get rom-object */
	    currentRom = DataStore.currentRom;
	    
	    /** check if a rom is set */
	    if(currentRom==null) {
	    	Util.alert(this, getString(R.string.lang_rom_selection_noRomSelected));
	    	return;
	    }
	    
	    /** get rom-options */
	    options = currentRom.getOptions();
	    
	    /** get categories */
	    ArrayList<PreferenceCategory> categories = new ArrayList<PreferenceCategory>();
	    ArrayList<String> categorieNames = new ArrayList<String>(); 
	    
	    /** loop through options */
	    for(int i=0; i<options.size(); i++) {
	    	OptionObject option = options.get(i);
	    	
	    	int categorieIndex = categorieNames.indexOf(option.getCategory());
    		if(categorieIndex<0) {
    			/** create index for category */
    			categorieNames.add(option.getCategory());
    			categorieIndex = categorieNames.indexOf(option.getCategory());
    			
    			/** create category-object */		
    			PreferenceCategory cat = new PreferenceCategory(this);
    			
    			/** set title */
    			cat.setTitle(option.getCategory());
    			
    			/** add categorie to arraylist and pref-screen */
    			categories.add(cat);
    			main.addPreference(cat);
    		}
	    	
	    	/** checkbox */
	    	if(option.getType().equals("checkbox")) {
	    		
	    		/** create preference */
	    		CheckBoxPreference p = new CheckBoxPreference(this);
	    		option.setPreference(p);
	    		
	    		/** set DisplayName */
	    		p.setTitle(option.getDisplayName());
	    		
	    		/** set summary */
	    		if(option.getSummary()!=null) p.setSummary(option.getSummary());
	    		
	    		/** set DefaultValue */
	    		if(option.getDefaultValue()==1) {
	    			p.setChecked(true);
	    		}
	    		else {
	    			p.setChecked(false);
	    		}
	    		
	    		/** add Preference to View */
	    		categories.get(categorieIndex).addPreference(p);
	    	}
	    	
	    	/** list */
	    	if(option.getType().equals("list")) {
	    		
	    		/** create preference */
	    		ListPreference p = new ListPreference(this);
	    		option.setPreference(p);
	    		
	    		/** set DisplayName */
	    		p.setTitle(option.getDisplayName());
	    		
	    		/** set summary */
	    		if(option.getSummary()!=null) p.setSummary(option.getSummary());
	    		
	    		/** set entrys */
	    		p.setEntries(option.getItems());
	    		p.setEntryValues(option.getValues());
	    		
	    		/** set DefaultValue */
	    		p.setValueIndex(option.getDefaultValue());
	    		
	    		/** add Preference to View */
	    		categories.get(categorieIndex).addPreference(p);
	    	}
	    }
	}
	
	private void addButtons() {

	    /** get Bottom-Layout */
	    LinearLayout bottomLayout = ma.getBottomLayout();
	    
	    /** create View */
	    LinearLayout linearLayout = new LinearLayout(bottomLayout.getContext());
	    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    
	    /** create layoutParams for buttons */
	    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    layoutParams.weight = 1.0f;
	    
	    /** create button1 */
	    this.buttonSave = new Button(linearLayout.getContext());
	    buttonSave.setLayoutParams(layoutParams);
	    buttonSave.setText(R.string.lang_optionSelection_buttonSave);
	    buttonSave.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				OptionSelectionActivity.this.onClickHandler(v);
			}
		});
	    linearLayout.addView(buttonSave);
	    
	    /** create button2 */
	    this.buttonReboot = new Button(linearLayout.getContext());
	    buttonReboot.setLayoutParams(layoutParams);
	    buttonReboot.setText(R.string.lang_optionSelection_buttonReboot);
	    buttonReboot.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				OptionSelectionActivity.this.onClickHandler(v);
			}
		});
	    linearLayout.addView(buttonReboot);
	    
	    /** add View to Bottom-Layout */
	    bottomLayout.addView(linearLayout);
	    
	    /** give Bottom-Layout the same background like PreferenceScreen */
	    bottomLayout.setBackgroundDrawable(getListView().getBackground());
	}
	
	private File generateNewFileName() {
		/** generate parameter from GUI */
		String parameter = "";
		for(int i=0; i<this.options.size(); i++) {
			OptionObject option = this.options.get(i);
			
			if(option.getType().equals("checkbox")) {
				CheckBoxPreference pref = (CheckBoxPreference)option.getPreference();
				
				if(pref.isChecked()) {
					parameter+="-"+option.getValue();
				}
			}
			else if(option.getType().equals("list")) {
				ListPreference pref = (ListPreference)option.getPreference();
				
				if(pref.getValue().length()>0) {
					parameter+="-"+pref.getValue();
				}
			}
			
		}
		
		/** create new file-object */
		File newFile = new File(this.currentRom.getFile().getParent(), this.currentRom.getRomName()+"-"+this.currentRom.getKernelVersion()+parameter+".zip");
		
		return newFile;
	}

	private boolean saveOptions() {
			
		/** get new file-object */
		File newFile = this.generateNewFileName();
		
		/** return if nothing changed */
		if(this.currentRom.isCurrentRomFile(newFile)) {
			Util.alert(this, getString(R.string.lang_error_nothingChanged));
			return false;
		}
		
		/** rename and check message */
		switch(this.currentRom.renameRomFile(newFile)) {
			case RomObject.RENAME_SUCCESS:
			return true;
			
			case RomObject.RENAME_ERROR_ALREADY_EXISTS:
				Util.alert(this, getString(R.string.lang_alert_renameFileAlreadyExists));
			return false;
			
			case RomObject.RENAME_ERROR_NOT_WRITABLE:
				Util.alert(this, getString(R.string.lang_alert_renameFileNotWritable));
			return false;
			
			case RomObject.RENAME_ERROR_UNKNOWN:
			default:
				Util.alert(this, getString(R.string.lang_error_unknownError));
			return false;
		}
	}
	
	private void doCrcCheck() {
		currentRom.checkCRC(OptionSelectionActivity.this, new RomObject.onCRCCheckFinished() {
			
			@Override
			public void onFinish(boolean success) {
				
				/** show Toast and request reboot */
				if(success) {
					Toast.makeText(OptionSelectionActivity.this, getString(R.string.lang_crcCheck_successMessage),Toast.LENGTH_LONG).show();
					requestReboot();
				}
				
				/** show Toast and cancel here */
				else {
					Toast.makeText(OptionSelectionActivity.this, getString(R.string.lang_crcCheck_errorMessage),Toast.LENGTH_LONG).show();
				}
				
				super.onFinish(success);
			}
		});
	}
	
	private void requestCrcCheck() {
		
		
		switch (DataStore.checkCRCBeforeReboot) {
		
			/** don't check CRC and directly request reboot */
			case 0:
				requestReboot();
			break;
			
			/** directly check CRC and reboot after that */
			case 1:
				doCrcCheck();
			break;
	
			/** ask the user what to do */
			default:
				Util.alertCustom(this, getString(R.string.lang_crcCheck_requestMessage), R.string.lang_alert_buttonYes, R.string.lang_alert_buttonNo, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {

							/** check CRC and request reboot */
							case DialogInterface.BUTTON_POSITIVE:
								doCrcCheck();
							break;
							
							/** directly request reboot */
							case DialogInterface.BUTTON_NEGATIVE:
								requestReboot();
							break;
						}
					}
				});
			break;
		}
	}
	
	private void requestReboot() {
		
		Util.alertOkCancel(this, getString(R.string.lang_alert_rebootText), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {

					case DialogInterface.BUTTON_POSITIVE:
						Util.rebootPhone();
					break;
				}
			}
		});
	}
	
	public void onClickHandler(View v) {
		
		if(v==OptionSelectionActivity.this.buttonSave) {
			if(this.saveOptions()) {
				Util.alert(this, getString(R.string.lang_alert_saveSuccess));
			}
		}
		
		else if(v==OptionSelectionActivity.this.buttonReboot) {
			
			/** get new file-object */
			File newFile = this.generateNewFileName();
			
			/** ask user to save if anything changed */
			if(!this.currentRom.isCurrentRomFile(newFile)) {
				Util.alertCustom(this, getString(R.string.lang_alert_saveRequest), R.string.lang_alert_buttonYes, R.string.lang_alert_buttonNo, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {
	
							/** save options and request crcCheck */
							case DialogInterface.BUTTON_POSITIVE:
								if(saveOptions()) {
									requestCrcCheck();
								}
							break;
							
							/** directly request crcCheck */
							case DialogInterface.BUTTON_NEGATIVE:
								requestCrcCheck();
							break;
						}
					}
				});
			}
			
			/** if nothing changed, request reboot */
			else {
				requestCrcCheck();
			}
		}
	}
}
