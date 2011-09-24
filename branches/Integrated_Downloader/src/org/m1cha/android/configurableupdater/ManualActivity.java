package org.m1cha.android.configurableupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

public class ManualActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** set layout */
		setContentView(R.layout.manual);
		
		/** show manual */
		this.showManual();
		
	}
	
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
	
	/** onClick-Handler */
    public void onClickHandler(View view) {
    	
    	switch(view.getId()) {
    		
    		case R.id.manual_buttonClose:
    			finish();
    		break;
    	}
    }
    
    private void showManual() {
		
		/** get manual */
        File manualFile = Util.getCustomFile("manual.html");
        
    	/** load manual */
    	String text = "";
    	try {
    		
    		/** load custom manual-file if avaible */
    		if(manualFile.canRead()) {
    			try {
    				FileInputStream is = new FileInputStream(manualFile);
    				text = Util.getStreamData(is);
    			} catch (FileNotFoundException e) {
    				Logger.debug("custom manual-file not found", e);
    			}
            }
    		
    		/** else load internal manual-file */
    		else {
    			InputStream stream = getResources().openRawResource(R.raw.manual);
    			text = Util.getStreamData(stream);
    		}
		} catch (IOException e) {
			Util.alert(this, getString(R.string.lang_error_loadManual));
		}
    	
    	/** load html-manual into webview */
        WebView manual_content = (WebView) findViewById(R.id.manual_content);
        manual_content.setBackgroundColor(Color.BLACK);
        manual_content.loadData(text,"texl/html","utf-8");
    }
}
