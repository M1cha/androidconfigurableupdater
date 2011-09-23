package org.m1cha.android.configurableupdater;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;


public class MainActivity extends TabActivity {
	
	private TabHost tabHost;
    
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /** show tabhost-layout */
        setContentView(R.layout.tabhost);
       
        tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, IntroActivity.class);
        spec = tabHost.newTabSpec("intro").setIndicator("Start").setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, DownloadActivity.class);
        spec = tabHost.newTabSpec("download").setIndicator("Download").setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, OptionSelectionActivity.class);
        spec = tabHost.newTabSpec("optionselection").setIndicator("Installieren").setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ManualActivity.class);
        spec = tabHost.newTabSpec("help").setIndicator("?").setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.getTabWidget().getChildAt(3).setLayoutParams(new 
        		LinearLayout.LayoutParams(tabHost.getTabWidget().getChildAt(3).getLayoutParams().height,tabHost.getTabWidget().getChildAt(3).getLayoutParams().height)); 

        tabHost.setCurrentTab(0);
    }
    
    public void setCurrentTab(int index) {
    	this.tabHost.setCurrentTab(index);
    }
    
    public View getCurrentTabView() {
    	return this.tabHost.getCurrentView();
    }
    
    public LinearLayout getBottomLayout() {
    	return (LinearLayout)findViewById(R.id.tabhost_layoutBottom);
    }
}