package org.m1cha.android.configurableupdater;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class IntroActivity extends Activity {

	private MainActivity ma;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** get parent */
		this.ma = (MainActivity)getParent();
		
		/** set layout */
		setContentView(R.layout.intro);
		
	}
	
	/** onClick-Handler */
    public void onClickHandler(View view) {
    	
    	switch(view.getId()) {
    		case R.id.intro_buttonManual:
    			ma.setCurrentTab(3);
    		break;
    		case R.id.intro_buttonNext:
    			ma.setCurrentTab(1);
    		break;
    	}
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

}
