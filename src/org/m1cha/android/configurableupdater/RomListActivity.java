package org.m1cha.android.configurableupdater;

import org.m1cha.android.configurableupdater.romtools.RomList;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class RomListActivity extends Activity {

	private MainActivity ma;
	public RomList romList;
	private String[] romNames;
	private Spinner spinner_roms;
	private String changelog;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** get parent */
		this.ma = (MainActivity)getParent();
		
		/** set layout */
		setContentView(R.layout.rom_selection);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		/** get romList */
		this.romList = new RomList(this, PreferenceManager.getDefaultSharedPreferences(this).getString("romfolder", Util.getDefaultRomFolder(this)));
		this.romNames = this.romList.getRomNames();
		
		/** activate instal-tab depending of roms were found */
		if(this.romNames.length<=0) {
			ma.setInstallTabEnabled(false);
		}
		else {
			ma.setInstallTabEnabled(true);
		}
		
		/** add roms to spinner */
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, romNames);
		this.spinner_roms = (Spinner)findViewById(R.id.rom_selection_buttonRoms);
		this.spinner_roms.setAdapter(spinnerArrayAdapter);
		
		/** onselected-Listener for spinner */
		this.spinner_roms.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> av, View v, int index, long l_index) {
				RomSelection_setRom(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	/** onClick-Handler */
    public void onClickHandler(View view) {
    	
    	switch(view.getId()) {
			case R.id.rom_selection_buttonNext:
				ma.setCurrentTab(2);
			break;
			case R.id.rom_selection_buttonChangelog:
				if(this.changelog!=null) {
					Util.showPopup(this, getString(R.string.lang_romChangelog_title), changelog);
				}
			break;
    	}
    }
    

    /**
     * update view so it show infos about the selected rom
     * @param index
     */
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
		findViewById(R.id.rom_selection_buttonNext).setEnabled(true);
	}
}
