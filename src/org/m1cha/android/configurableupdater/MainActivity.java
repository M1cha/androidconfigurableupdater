package org.m1cha.android.configurableupdater;

import java.io.IOException;
import java.io.InputStream;

import org.m1cha.android.configurableupdater.romtools.RomList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
	private int currentView = R.layout.intro;
	private RomList romList;
	private String[] romNames;
	private Spinner spinner_roms;
	public static String TAG = "UPDATER";
	private String changelog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
        	currentView = savedInstanceState.getInt("currentView");
        }
        showView(currentView);
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
    			Util.alert(this, "Not implemented!");
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
		this.romList = new RomList(this, "miuiupdater");
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
        
	}
}