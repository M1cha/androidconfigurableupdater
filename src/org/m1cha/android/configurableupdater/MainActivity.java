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
        
        /** restore saved instance */
        if(savedInstanceState!=null) {
        	currentView = savedInstanceState.getInt("currentView");
        }
        
        /** shared preferences */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        /** get rom-folder */
        MainActivity.romFolder = preferences.getString("romfolder", getString(R.string.default_romFolder));
        
        /** create and show flipper */
        li = LayoutInflater.from(this);
        flipper = new ViewFlipper(this);
        setContentView(flipper);
        
        /** show view */
        showView(R.layout.intro, ANIM_DISABLED);
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
    	
    }
    
    public static void reloadRomSelection() {
    	object.showView(R.layout.rom_selection, object.ANIM_DISABLED);
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