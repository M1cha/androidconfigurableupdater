package org.m1cha.android.configurableupdater;

import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

public class MainActivity extends Activity {
	
	private int currentView = R.layout.intro;
	
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
    		
    		case R.id.rom_selection_spinnerRoms:
    		break;
    		case R.id.rom_selection_buttonNext:
    			Util.alert(this, "Not implemented!");
    		break;
    		case R.id.rom_selection_buttonChangelog:
    			Util.alert(this, "Not implemented!");
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
}