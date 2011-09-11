package org.m1cha.android.configurableupdater;

import java.io.IOException;
import java.io.InputStream;
import org.m1cha.android.configurableupdater.romtools.RomList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
	private int currentView = R.layout.intro;
	public RomList romList;
	private String[] romNames;
	private Spinner spinner_roms;
	private String changelog;
	private SharedPreferences preferences;
	private static String romFolder;
	private static MainActivity object;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /** save this to an static object
         *  not good but needed for 'reloadRomSelection'
         */
        object = this;
        
        /** restore saved instance */
        if(savedInstanceState!=null) {
        	currentView = savedInstanceState.getInt("currentView");
        }
        
        /** shared preferences */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        /** get rom-folder */
        MainActivity.romFolder = preferences.getString("romfolder", getString(R.string.default_romFolder));
        
        /** show view */
        showView(currentView);
    }
    
    public static void setRomFolder(String s) {
    	MainActivity.romFolder = s;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
    	outState.putInt("currentView", currentView);
    	super.onSaveInstanceState(outState);
    };

    /** Needed for Back-Button-Handling */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	switch(currentView) {
        		case R.layout.manual:
        			showView(R.layout.intro);
        		break;
        		case R.layout.rom_selection:
        			showView(R.layout.intro);
        		break;
        		default:
        			return super.onKeyDown(keyCode, event);
        	}
        }
        return false;
    }
    
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
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    /** onClick-Handler */
    public void onClickHandler(View view) {
    	
    	switch(view.getId()) {
    		case R.id.intro_buttonManual:
    			showView(R.layout.manual);
    		break;
    		case R.id.intro_buttonNext:
    			showView(R.layout.rom_selection);
    		break;
    		
    		case R.id.manual_buttonNext:
    			showView(R.layout.rom_selection);
    		break;
    		
    		case R.id.rom_selection_buttonRoms:
    			this.spinner_roms.performClick();
    		break;
    		case R.id.rom_selection_buttonNext:
    			Intent i = new Intent(this, OptionSelection.class);
    			startActivity(i);
    		break;
    		case R.id.rom_selection_buttonChangelog:
    			if(this.changelog!=null) {
    				Util.showPopup(this, getString(R.string.lang_romChangelog_title), changelog);
    			}
    		break;
    	}
    }
    
    private void showView(int layoutID) {
    	/** save, which view we are using now */
    	currentView = layoutID;
    	
    	/** show the new view */
    	setContentView(layoutID);
    	
    	/** set layout's title */
    	String title = Util.getTitleByLayoutId(this, layoutID);
    	if(title==null) title = getString(R.string.app_name);
    	setTitle(title);
    	
    	/** init-function for some special-pages */
    	if(layoutID==R.layout.manual) {
    		showManual();
    	}
    	else if(layoutID==R.layout.rom_selection) {
    		showRomSelection();
    	}
    	
    }
    
    public static void reloadRomSelection() {
    	object.showView(R.layout.rom_selection);
    }

	private void showManual() {
    	/** load manual */
    	String text = "";
    	try {
    		InputStream stream = getResources().openRawResource(R.raw.manual);
			text = Util.getStreamData(stream);
		} catch (IOException e) {
			Util.alert(this, getString(R.string.lang_error_loadManual));
		}
    	
    	/** load html-manual into webview */
        WebView manual_content = (WebView) findViewById(R.id.manual_content);
        manual_content.setBackgroundColor(Color.BLACK);
        manual_content.loadData(text,"texl/html","utf-8");
    }
	
	private void showRomSelection() {
		
		/** get romList */
		this.romList = new RomList(this, MainActivity.romFolder);
		this.romNames = this.romList.getRomNames();
		
		/** add roms to spinner */
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, romNames);
		this.spinner_roms = (Spinner)findViewById(R.id.rom_selection_buttonRoms);
		this.spinner_roms.setAdapter(spinnerArrayAdapter);
		
		/** onclick-Listener for spinner */
		this.spinner_roms.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> av, View v, int index, long l_index) {
				MainActivity.this.RomSelection_setRom(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	public void RomSelection_setRom(int index) {
		
		/** set cover */
		ImageView myImage = (ImageView) findViewById(R.id.rom_selection_cover);
		if(this.romList.getRom(index).getCover()!=null) {
			myImage.setImageBitmap(this.romList.getRom(index).getCover());
		}
		else {
			myImage.setImageResource(R.drawable.nocover);
		}
		
		/** set changelog */
		this.changelog = this.romList.getRom(index).getChangelog();
		if(changelog.length()>0) {
			findViewById(R.id.rom_selection_buttonChangelog).setEnabled(true);
		}
		else {
			findViewById(R.id.rom_selection_buttonChangelog).setEnabled(false);
		}
		
		/** set current ROM */
		DataStore.currentRom = this.romList.getRom(index);
        
	}
}