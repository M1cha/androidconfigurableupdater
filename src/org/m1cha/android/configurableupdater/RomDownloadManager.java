package org.m1cha.android.configurableupdater;

import java.util.ArrayList;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class RomDownloadManager {

	private DownloadManager dm;
	private ArrayList<DownloadRequest> downloads = new ArrayList<DownloadRequest>();
	private onDownloadStatusListener listener;
	private Context context;
	
	public RomDownloadManager(Context context) {
		
		/** get download service */
		this.dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		
		this.context = context;
	}
	
	public long newDownload(String url) {

		
		/** prepare download-request */
		DownloadManager.Request request = new DownloadManager.Request(
			Uri.parse(url)
		);
		
		/** request download */
		long enqueue = this.dm.enqueue(request);
		
		/** store download-id in arraylist */
		this.downloads.add(new DownloadRequest(this, enqueue));

		/** return download-ID */
		return enqueue;
	}
	
	public void setDownloadStatusListener(onDownloadStatusListener l) {
		this.listener = l;
	}
	public onDownloadStatusListener getDownloadStatusListener() {
		return this.listener;
	}
	
	public DownloadManager getDownloadManager() {
		return this.dm;
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public ArrayList<DownloadRequest> getDownloads() {
		return this.downloads;
	}
}
