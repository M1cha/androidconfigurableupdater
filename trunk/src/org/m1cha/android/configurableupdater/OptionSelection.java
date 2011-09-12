package org.m1cha.android.configurableupdater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.m1cha.android.configurableupdater.romtools.OptionObject;
import org.m1cha.android.configurableupdater.romtools.RomObject;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class OptionSelection extends PreferenceActivity {

	public Button buttonSave, buttonReboot = null;
	private RomObject currentRom;
	private ArrayList<OptionObject> options;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
    	switch(item.getItemId()) {
    		case R.id.menuMain_itemSettings:
    			Intent i = new Intent(this, MainPreferenceActivity.class);
    			startActivity(i);
    		break;
    		
    		case R.id.menuMain_itemFeedback:
    			/** receiver */
    			String[] mailto = { "m1cha-dev@web.de" };
    			
    		    /** create intent */
    		    Intent sendIntent = new Intent(Intent.ACTION_SEND);
    		    
    		    /** set attributes */
    		    sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
    		    sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lang_menuMain_itemFeedSubject));
    		    sendIntent.putExtra(Intent.EXTRA_TEXT, "");
    		    sendIntent.setType("text/plain");
    		    
    		    /** start */
    		    startActivity(Intent.createChooser(sendIntent, getString(R.string.lang_menuMain_itemFeedSubject)));
    		break;
    		
    		case R.id.menuMain_itemAbout:
    			/** load about-text */
    	    	String text = "";
    	    	try {
    	    		InputStream stream = getResources().openRawResource(R.raw.about);
    				text = Util.getStreamData(stream);
    			} catch (IOException e) {
    				Util.alert(this, getString(R.string.lang_error_uncaughtException));
    			}
    	    	
    	    	/** show popup */
    			Util.showPopup(this, getString(R.string.lang_menuMain_itemAbout), text);
    			
    		break;
    	};
    	
    	return super.onOptionsItemSelected(item);
    }
	

    /** Needed for Back-Button-Handling */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	MainActivity.reloadRomSelection();
        }
    
    	return super.onKeyDown(keyCode, event);
    }
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    /** set title */
	    setTitle(R.string.lang_optionSelection_title);
	    
	    /** load preference-xml */
	    addPreferencesFromResource(R.xml.pref_option_selection);
	    PreferenceScreen main = ((PreferenceScreen)findPreference("PREFSMAIN"));
	    
	    /** add buttons */
	    this.addButtons();
	    
	    /** get rom-object */
	    currentRom = DataStore.currentRom;
	    options = currentRom.getOptions();
	    
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

	    /** create View */
	    LinearLayout prefRoot = Util.getPrefRoot(getWindow());
	    LinearLayout linearLayout = new LinearLayout(prefRoot.getContext());
	    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    
	    /** create layoutParams for buttons */
	    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    layoutParams.weight = 1.0f;
	    
	    /** create button1 */
	    this.buttonSave = new Button(prefRoot.getContext());
	    buttonSave.setLayoutParams(layoutParams);
	    buttonSave.setText(R.string.lang_optionSelection_buttonSave);
	    buttonSave.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				OptionSelection.this.onClickHandler(v);
			}
		});
	    linearLayout.addView(buttonSave);
	    
	    /** create button2 */
	    this.buttonReboot = new Button(prefRoot.getContext());
	    buttonReboot.setLayoutParams(layoutParams);
	    buttonReboot.setText(R.string.lang_optionSelection_buttonReboot);
	    buttonReboot.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				OptionSelection.this.onClickHandler(v);
			}
		});
	    linearLayout.addView(buttonReboot);
	    
	    /** add View to Root-Layout */
	    prefRoot.addView(linearLayout);
	}
	
	public void onClickHandler(View v) {
		
		if(v==OptionSelection.this.buttonSave) {
			
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
			File newFile = new File(this.currentRom.getFile().getParent(), this.currentRom.getRomName()+parameter+".zip");
			
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
			
			/** show message */
			Util.alert(this, getString(R.string.lang_menuMain_msgRestore));
		}
		
		else if(v==OptionSelection.this.buttonReboot) {
			Util.alert(this, "Not implemented!");
		}
	}
}
