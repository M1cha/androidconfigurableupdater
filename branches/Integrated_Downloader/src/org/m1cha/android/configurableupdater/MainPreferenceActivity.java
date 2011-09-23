package org.m1cha.android.configurableupdater;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainPreferenceActivity extends PreferenceActivity {
	
	private Button buttonReset;
	private EditTextPreference prefRomlist;
	private SharedPreferences sharedPref;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    /** get shared prefManager */
	    this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    /** load preference-xml */
	    addPreferencesFromResource(R.xml.pref_main_menu);
	    
	    /** get romEdit-control */
	    this.prefRomlist = (EditTextPreference) findPreference("romfolder");
	    
	    /** onchange-listener for reset-button */
	    this.sharedPref.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				Logger.debug("global_onchange");
				
				/** reload */
				reloadPrefScreen();
			}
		});
	    
	    /** onchange-listener for user-edit */
	    this.prefRomlist.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Logger.debug("romlist_onchange");

				/** reload */
				reloadPrefScreen();
				
				return true;
			}
		});
	    
	    /** add reset-button */
	    this.addResetButton();
	}
	
	private void reloadPrefScreen() {
		
		/** get new value */
		String newValue = sharedPref.getString("romfolder", Util.getDefaultRomFolder());
		
		/** tell romFolder to MainActivity */
		MainActivity.setRomFolder(newValue);
		
		/** re-render pref-screen */
		getPreferenceScreen().removeAll();
		addPreferencesFromResource(R.xml.pref_main_menu);
	}

	
	private void addResetButton() {

	    /** create View */
	    LinearLayout prefRoot = Util.getPrefRoot(getWindow());
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
	
	public void onClickHandler(View v) {
		if(v==MainPreferenceActivity.this.buttonReset) {
			/** set romfolder-value to default */
			this.sharedPref.edit().putString("romfolder", Util.getDefaultRomFolder()).commit();
			
			/** show message */
			Util.alert(this, getString(R.string.lang_menuMain_msgRestore));
		}
	}
}
