package org.m1cha.android.configurableupdater;

import java.io.File;
import java.util.ArrayList;
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
	    main = ((PreferenceScreen)findPreference("PREFSMAIN"));
	    
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
	
	public void onClickHandler(View v) {
		
		if(v==OptionSelectionActivity.this.buttonSave) {
			
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
					Logger.debug(pref.getValue());
				}
				
			}
			
			/** create new file-object */
			File newFile = new File(this.currentRom.getFile().getParent(), this.currentRom.getRomName()+"-"+this.currentRom.getKernelVersion()+parameter+".zip");
			
			/** check if anything was changed */
			if(newFile.getName().equals(this.currentRom.getFile().getName())) {
				Util.alert(this, getString(R.string.lang_error_nothingChanged));
				return;
			}
			
			/** check if we can write */
			if(!this.currentRom.getFile().canWrite()) {
				Util.alert(this, getString(R.string.lang_error_writeNewFile));
				return;
			}
			
			/** check if file already exists */
			if(newFile.exists()) {
				Util.alert(this, getString(R.string.lang_error_fileExists));
				return;
			}
			
			/** rename file */
			this.currentRom.getFile().renameTo(newFile);
			this.currentRom.setFile(newFile);
			
			/** show message */
			Util.alert(this, getString(R.string.lang_menuMain_msgRestore));
		}
		
		else if(v==OptionSelectionActivity.this.buttonReboot) {
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
	}
}
