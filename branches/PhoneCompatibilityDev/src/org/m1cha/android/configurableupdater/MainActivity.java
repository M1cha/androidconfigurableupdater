package org.m1cha.android.configurableupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.m1cha.android.configurableupdater.romtools.RomList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {
	
	private int currentView;
	public RomList romList;
	private String[] romNames;
	private Spinner spinner_roms;
	private String changelog;
	private SharedPreferences preferences;
	private static String romFolder;
	private static MainActivity object;
	private LayoutInflater li;
	private ViewFlipper flipper;
	private View currentViewObject;
	private boolean cameFromManual = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /** save this to an static object
         *  not good but needed for 'reloadRomSelection'
         */
        object = this;
        
        /** set intro as first view */
        int firstViewId = R.layout.intro;
        
        /** restore saved instance */
        if(savedInstanceState!=null) {
        	firstViewId = savedInstanceState.getInt("currentView");
        }
        
        /** shared preferences */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        /** set default rom-folder */
        if(android.os.Build.MODEL.equals("Optimus 2X")) {
        	Util.setDefaultRomFolder("_ExternalSD/"+getString(R.string.default_romFolder));
    	}
        else {
        	Logger.debug("Executing on device: "+android.os.Build.MODEL);
        	Logger.debug("using internal default-romfolder");
        	Util.setDefaultRomFolder(getString(R.string.default_romFolder));
        }
        
        /** store romfolder statically in this class */
        MainActivity.romFolder = preferences.getString("romfolder", Util.getDefaultRomFolder());
        
        /** create and show flipper */
        li = LayoutInflater.from(this);
        flipper = new ViewFlipper(this);
        setContentView(flipper);
        
        /** show view */
        showView(firstViewId, ANIM_DISABLED);
        
        ArrayList<MountPoint> mountPoints = Util.getMountPoints();
        for(int i=0; i<mountPoints.size(); i++) {
        	if(mountPoints.get(i).getDevice().equals("/dev/block/vold/179:9")) {
        		Util.alert(this, "SDCard is mountet at: "+mountPoints.get(i).getMountPoint());
        	}
        }
        
    }
    
    public static void setRomFolder(String s) {
    	MainActivity.romFolder = s;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
    	outState.putInt("currentView", currentView);
    	super.onSaveInstanceState(outState);
    };
    
    /** 
     * redirect this method to the current View 
     * Otherwise it would go to the flipper
     */
    @Override
    public View findViewById(int id) {
    	return this.currentViewObject.findViewById(id);
    };

    /** Needed for Back-Button-Handling */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	switch(currentView) {
        		case R.layout.manual:
        			showView(R.layout.intro, ANIM_LEFT2RIGHT);
        		break;
        		case R.layout.rom_selection:
        			if(this.cameFromManual) {
        				showView(R.layout.manual, ANIM_LEFT2RIGHT);
        			}
        			else {
        				showView(R.layout.intro, ANIM_LEFT2RIGHT);
        			}
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
    	Util.menuHandler(this, item);
    	return super.onOptionsItemSelected(item);
    }
    
    /** onClick-Handler */
    public void onClickHandler(View view) {
    	
    	switch(view.getId()) {
    		case R.id.intro_buttonManual:
    			showView(R.layout.manual, ANIM_RIGHT2LEFT);
    		break;
    		case R.id.intro_buttonNext:
    			this.cameFromManual = false;
    			showView(R.layout.rom_selection, ANIM_RIGHT2LEFT);
    		break;
    		
    		case R.id.manual_buttonNext:
    			this.cameFromManual = true;
    			showView(R.layout.rom_selection, ANIM_RIGHT2LEFT);
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
    
	private Animation inFromRightAnimation() {
	
		Animation inFromRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromRight.setDuration(200);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoLeft.setDuration(200);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	
	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(200);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	
	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoRight.setDuration(200);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
	private int ANIM_DISABLED = 0;
    private int ANIM_LEFT2RIGHT = 1;
    private int ANIM_RIGHT2LEFT = 2;
    private void showView(int layoutID, int direction) {
       
		/** load new view */
    	View newView = li.inflate(layoutID, null);
    	
    	/** if new view is already the actual one do nothing */
    	if(layoutID!=currentView) {
    		
    		/** add new view */
			flipper.addView(newView);
			   
			/** show new view */
			if(direction==ANIM_DISABLED) {
				flipper.setInAnimation(null);
				flipper.setOutAnimation(null);
				flipper.setDisplayedChild(0);
			}
			else {
				if(direction==ANIM_RIGHT2LEFT) {
					flipper.setInAnimation(inFromRightAnimation());
					flipper.setOutAnimation(outToLeftAnimation());
				}
				else if(direction==ANIM_LEFT2RIGHT) {
					flipper.setInAnimation(inFromLeftAnimation());
					flipper.setOutAnimation(outToRightAnimation());
				}
				flipper.showNext();
			}
	   
			/** delete old view if there is one */
			if(flipper.getChildCount()>1) {
				/** remove old view */
				flipper.removeView(li.inflate(currentView, null));
			}
			
			/** save, which view we are using now */
	    	currentView = layoutID;
	    	currentViewObject = newView;
    	}
    	
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
    	else if(layoutID==R.layout.intro) {
    		showIntro();
    	}
    	
    }
    
    public static void reloadRomSelection() {
    	object.showView(R.layout.rom_selection, object.ANIM_DISABLED);
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
	
	private void showIntro() {
		
        /** get custom intro-image if avaible */
        File introPicture = Util.getCustomFile("intro.png");
        if(!introPicture.canRead()) {
        	Logger.debug("cannot read custom intro-image");
        	return;
        }
        
        /** try to load and display image */
        try {
			FileInputStream is = new FileInputStream(introPicture);
			Bitmap bm = BitmapFactory.decodeStream(is);
			ImageView iv = (ImageView)findViewById(R.id.intro_image);
			iv.setImageBitmap(bm);
		} catch (FileNotFoundException e) {
			Logger.debug("custom intro-image not found", e);
		}
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
		findViewById(R.id.rom_selection_buttonNext).setEnabled(true);
        
	}
}