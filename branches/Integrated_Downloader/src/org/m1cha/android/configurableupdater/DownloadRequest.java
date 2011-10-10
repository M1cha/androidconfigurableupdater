package org.m1cha.android.configurableupdater;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

public class DownloadRequest {

	private RomDownloadManager rdm;
	private long id;
	private BroadcastReceiver receiver;
	private int status=-1;
	private boolean running = true;
	
	public DownloadRequest(RomDownloadManager rdm, long id) {
		
		this.rdm = rdm;
		this.id = id;
		this.createListener();
	}
	
	private void createListener() {
		
		/** listener for running download */
        this.receiver = new BroadcastReceiver() {
        	
            @Override
            public void onReceive(Context context, Intent intent) {
            	
            	/** get action */
                String action = intent.getAction();
                
                /** check if download is completed */
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                  
					/** filter downloads */
					Query query = new Query();
					query.setFilterById(id);
					Cursor c = rdm.getDownloadManager().query(query);
					  
					/** if our download is in the list */
					if (c.moveToFirst()) {
					 	
						/** get column-index */
						int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
						  
						/** save status */
						status = c.getInt(columnIndex);
						running = false;
						  
						
					}
                }
					
				/** call listener */
				if(rdm.getDownloadStatusListener()!=null) {
					rdm.getDownloadStatusListener().onStatusChanged();
				}
            }
        };
        
        /** register receiver */
        this.rdm.getContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public boolean isRunning() {
		return this.running;
	}
}
