package org.m1cha.android.configurableupdater.activities;

import org.m1cha.android.configurableupdater.DataStore;
import org.m1cha.android.configurableupdater.R;
import org.m1cha.android.configurableupdater.Util;
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
		
		/** find spinner */
		this.spinner_roms = (Spinner)findViewById(R.id.rom_selection_buttonRoms);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		/** disable install-tab */
		ma.setInstallTabEnabled(false);
		
		/** disable spinner */
		this.spinner_roms.setEnabled(false);
		
		/** remove listener */
		this.spinner_roms.setOnItemSelectedListener(null);
		
		/** save currently selected ROM if available */
		int oldSelectionIndex = this.spinner_roms.getSelectedItemPosition();
		String oldSelectionName = null;
		if(oldSelectionIndex!=Spinner.INVALID_POSITION) {
			oldSelectionName = this.romList.getRom(oldSelectionIndex).getRomName();
		}
		
		/** get romList */
		this.romList = new RomList(this, PreferenceManager.getDefaultSharedPreferences(this).getString("romfolder", Util.getDefaultRomFolder(this)));

		/** check for errors */
		boolean abort=true;
		switch(this.romList.getLastestError()) {
			case RomList.ERROR_EXTERNAL_STORAGE_CANNOT_READ:
				Util.alert(this, getString(R.string.lang_error_sdcardCannotRead));
			break;
			
			case RomList.ERROR_DIRECTORY_NOT_FOUND:
				Util.alert(this, getString(R.string.lang_error_directoryNotFound));
			break;
				
			case RomList.ERROR_NOT_A_DIRECTORY:
				Util.alert(this, getString(R.string.lang_error_notADirectory));
			break;
				
			case RomList.ERROR_DIRECTORY_CANNOT_READ:
				Util.alert(this, getString(R.string.lang_error_directoryCannotRead));
			break;
			
			default:
				Util.alert(this, getString(R.string.lang_error_unknownError));
			break;
			
			case RomList.NO_ERROR:
				abort=false;
			break;
		}
		
		/** get romNames */
		this.romNames = this.romList.getRomNames();
		
		/** disable install-Tab if no ROM's were found */
		if(this.romNames.length<=0) {
			abort=true;
		}
		
		/** add ROM's to spinner */
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, romNames);
		this.spinner_roms.setAdapter(spinnerArrayAdapter);
		
		/** return if errors occurred */
		if(abort) {
			this.RomSelection_noRoms();
			return;
		}
		
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
		
		/** set current selection to old selection if there was one */
		if(oldSelectionName!=null) {
			for(int i=0; i<spinnerArrayAdapter.getCount(); i++) {
				if(this.romList.getRom(i).getRomName().equals(oldSelectionName)) {
					this.spinner_roms.setSelection(i, true);
				}
			}
		}
		
		/** enable spinner */
		this.spinner_roms.setEnabled(true);
		
		/** enable install-Tab */
		ma.setInstallTabEnabled(true);
	}
	
	/** onClick-Handler */
    public void onClickHandler(View view) {
    	
    	switch(view.getId()) {
			case R.id.rom_selection_buttonNext:
				ma.setCurrentTab(1);
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
	
	private void RomSelection_noRoms() {
		
		/** set cover */
		ImageView myImage = (ImageView) findViewById(R.id.rom_selection_cover);
		myImage.setImageResource(R.drawable.norom);
		
		/** disable buttons */
		findViewById(R.id.rom_selection_buttonChangelog).setEnabled(false);
		findViewById(R.id.rom_selection_buttonNext).setEnabled(false);
		
		/** delete currentRom from datastore */
		DataStore.currentRom = null;
	}
}
