package org.m1cha.android.configurableupdater;

import java.util.ArrayList;

import org.m1cha.android.configurableupdater.romtools.OptionObject;
import org.m1cha.android.configurableupdater.romtools.RomObject;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class OptionSelection extends PreferenceActivity {

	public Button buttonSave, buttonReboot = null;
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch(item.getItemId()) {
//			case R.id.menuMain_itemExit:
//				System.exit(1);
//			break;
//		}
//		
//		return true;
//	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    /** load preference-xml */
	    addPreferencesFromResource(R.xml.pref_option_selection);
	    PreferenceScreen main = ((PreferenceScreen)findPreference("PREFSMAIN"));
	    
	    /** add buttons */
	    this.addButtons();
	    
	    /** get rom-object */
	    RomObject currentRom = DataStore.currentRom;
	    ArrayList<OptionObject> options = currentRom.getOptions();
	    
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
			Util.alert(v.getContext(), "Nicht implementiert!");
		}
		
		else if(v==OptionSelection.this.buttonReboot) {
			Util.alert(v.getContext(), "Nicht implementiert!");
		}
	}
}
