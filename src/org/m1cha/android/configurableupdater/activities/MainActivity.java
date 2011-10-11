package org.m1cha.android.configurableupdater.activities;

import org.m1cha.android.configurableupdater.R;
import org.m1cha.android.configurableupdater.Util;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TabHost;


public class MainActivity extends TabActivity {
	
	private TabHost tabHost;
	private boolean installTabEnabled=true;
    
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /** show tabhost-layout */
        setContentView(R.layout.tabhost);
       
        tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, RomListActivity.class);
        spec = tabHost.newTabSpec("romlist").setIndicator("Rom-Liste").setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, OptionSelectionActivity.class);
        spec = tabHost.newTabSpec("optionselection").setIndicator("Installieren").setContent(intent);
        tabHost.addTab(spec);
        
        /** disable install-tab */
		tabHost.getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					if(!getInstallTabEnabled()) {
						Util.alert(v.getContext(), getString(R.string.lang_rom_selection_noRomSelected));
					}
					else {
						setCurrentTab(1);
					}
				}
			});
        this.setInstallTabEnabled(false);

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
    
    
    public void setInstallTabEnabled(boolean b) {
    	this.installTabEnabled = b;
    }
    public boolean getInstallTabEnabled() {
    	return this.installTabEnabled;
    }
}