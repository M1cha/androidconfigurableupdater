package org.m1cha.android.configurableupdater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DownloadActivity extends Activity {

	private MainActivity ma;
    private RomDownloadManager rdm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** get parent */
		this.ma = (MainActivity)getParent();
		
		/** set layout */
		setContentView(R.layout.download);
		
//		/** create romDownloadManager */
//		this.rdm = new RomDownloadManager(this);
//		
//		/** add listener */
//		this.rdm.setDownloadStatusListener(new onDownloadStatusListener() {
//			@Override
//			public void onStatusChanged() {
//				// TODO Auto-generated method stub
//				reloadDownloadStatus();
//			}
//		});
		
		JSONObject roms = this.getUrlAsJsonObject("http://www.einsteinno1.de/android/ota/defy/roms.json");
		
		
	}
	
	
	
	private JSONObject getUrlAsJsonObject(String url) {
		try {
			String s = Util.getStringFromUrl(url);
			return new JSONObject(s);
		} catch (Exception e) {
			Logger.debug("DownloadActivity:getStringFromUrl", e);
			return null;
		}
	}
	
//	/** start download */
//	public void onClick(View view) {
//		rdm.newDownload("http://www.vogella.de/img/lars/LarsVogelArticle7.png");
//		reloadDownloadStatus();
//	}
//	
//	/** reload download-status */
//	private void reloadDownloadStatus() {
//		TextView tv = (TextView)findViewById(R.id.textView1);
//		String txt = "";
//		ArrayList<DownloadRequest> al = rdm.getDownloads();
//		
//		for(int i=0; i<al.size(); i++) {
//			DownloadRequest dr = al.get(i);
//			
//			if(dr.isRunning() || dr.getStatus()==DownloadManager.STATUS_RUNNING) {
//				txt+="Running\r\n";
//			}
//			else {
//				switch(dr.getStatus()) {
//					case DownloadManager.STATUS_SUCCESSFUL:
//						txt+="Success\r\n";
//					break;
//					case DownloadManager.STATUS_PENDING:
//						txt+="Pending\r\n";
//					break;
//					case DownloadManager.STATUS_PAUSED:
//						txt+="Paused\r\n";
//					break;
//					case DownloadManager.STATUS_FAILED:
//						txt+="Failed\r\n";
//					break;
//				}
//			}
//			
//		}
//		
//		tv.setText(txt);
//		
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//	
//	/** onClick-Handler */
//    public void onClickHandler(View view) {
//    	
//    	switch(view.getId()) {
//    		case R.id.intro_buttonManual:
//    			ma.setCurrentTab(3);
//    		break;
//    		case R.id.intro_buttonNext:
//    			ma.setCurrentTab(1);
//    		break;
//    	}
//    }
    
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
