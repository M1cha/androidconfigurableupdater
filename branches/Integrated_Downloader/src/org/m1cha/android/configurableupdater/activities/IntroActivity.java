package org.m1cha.android.configurableupdater.activities;

import org.m1cha.android.configurableupdater.Logger;
import org.m1cha.android.configurableupdater.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class IntroActivity extends Activity {

	private int delay = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** remove titlebar */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		/** set layout */
		setContentView(R.layout.intro);
		
		/** Create an object of type SplashHandler */
        SplashHandler mHandler = new SplashHandler();
		
		/** Create a Message object */
        Message msg = new Message();
        
        /** 
         * Assign a unique code to the message.
         * Later, this code will be used to identify the message in Handler class.
         */
        msg.what = SplashHandler.MESSAGE_TIMEOUT;
        
        /** disable intro in debug-mode */
        if(Logger.DEBUG) {
        	this.delay=0;
        }
        
        /** send message with delay */
        mHandler.sendMessageDelayed(msg, this.delay);
		
	}
	
	/** Handler class which receives messages */
	private class SplashHandler extends Handler {
		
		public static final int MESSAGE_TIMEOUT = 25863875;
	    
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
				case MESSAGE_TIMEOUT:
					super.handleMessage(msg);
					  
					/** create new intent */
					Intent intent = new Intent();
					
					/** set MainActivity as class */
					intent.setClass(IntroActivity.this, MainActivity.class);
					
					/** start activity */
					startActivity(intent);

					/** finish IntroActivity */
					IntroActivity.this.finish();
				break;
			}
		}
	}

}
