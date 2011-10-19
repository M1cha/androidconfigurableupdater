package org.m1cha.android.configurableupdater.activities;

import java.util.Map;

import org.m1cha.android.configurableupdater.R;
import org.m1cha.android.configurableupdater.Util;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private Button buttonReset;
	private EditTextPreference prefRomlist;
	
	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    /** load preference-xml */
	    addPreferencesFromResource(R.xml.pref_main_menu);
	    
	    /** get romEdit-control and set defaultValue */
	    this.prefRomlist = (EditTextPreference) findPreference("romfolder");
	    this.prefRomlist.setDefaultValue(Util.getDefaultRomFolder(this));
	    
	    /** add reset-button */
	    this.addResetButton();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();

	    /** create Map with all preferences on this screen */
	    Map<String, ?> sharedPreferencesMap = getPreferenceScreen().getSharedPreferences().getAll();
	    
	    /** update all preferences-summaries */
	    for (Map.Entry<String, ?> entry : sharedPreferencesMap.entrySet()) {
	    	this.updateSummaries(findPreference(entry.getKey()));
	    }

	    /** set listener so we know when a value has changed */          
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
	    super.onPause();

	    /** remove listener set by 'onResume()' */     
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
	}
	
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    this.updateSummaries(findPreference(key));
	}
	
	public void updateSummaries(Preference pref) {
		if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	    }
	    
	    else if (pref instanceof EditTextPreference) {
        	EditTextPreference editTextPref = (EditTextPreference) pref;
            pref.setSummary(editTextPref.getText());
        }
	}
	
	
	/**
	 * add reset-button to bottom of the screen
	 */
	private void addResetButton() {

	    /** create View */
	    LinearLayout prefRoot = Util.patchListView(getListView());
	    LinearLayout linearLayout = new LinearLayout(prefRoot.getContext());
	    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    
	    /** create layoutParams for buttons */
	    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    layoutParams.weight = 1.0f;
	    
	    /** create reset-button */
	    this.buttonReset = new Button(prefRoot.getContext());
	    buttonReset.setLayoutParams(layoutParams);
	    buttonReset.setText(R.string.lang_menuMain_buttonReset);
	    buttonReset.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainPreferenceActivity.this.onClickHandler(v);
			}
		});
	    linearLayout.addView(buttonReset);
	    
	    /** add View to Root-Layout */
	    prefRoot.addView(linearLayout);
	}
	
	
	/** 
	 * onclickHandler for the reset-button 
	 */
	public void onClickHandler(View v) {
		if(v==MainPreferenceActivity.this.buttonReset) {
			
			/** find romfolder-preference */
			EditTextPreference pref = (EditTextPreference)findPreference("romfolder");
			
			/** set default-value */
			pref.setText(Util.getDefaultRomFolder(this));
			
			/** show message */
			Util.alert(this, getString(R.string.lang_menuMain_msgRestore));
		}
	}
}
