package org.m1cha.android.configurableupdater.donate.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.m1cha.android.configurableupdater.donate.R;
import org.m1cha.android.configurableupdater.donate.Logger;
import org.m1cha.android.configurableupdater.donate.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;

public class IntroActivity extends Activity {

	private int delay = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** remove titlebar */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		/** set layout */
		setContentView(R.layout.intro);
		
		/** get custom intro-image if available */
        File introPicture = Util.getCustomFile("intro.png");
        if(introPicture.canRead() && introPicture.exists()) {
            
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
        else {
        	Logger.debug("no custom intro-image found or not readable!");
        }
        
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
