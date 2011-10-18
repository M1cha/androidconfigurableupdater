package org.m1cha.android.configurableupdater;

public class MountPoint {
	
	private String device;
	private String mountPoint;
	private String fsType;
	
	public MountPoint(String device, String mountPoint, String fsType) {
		this.device = device;
		this.mountPoint = mountPoint;
		this.fsType = fsType;
	}
	
	public String getDevice() {
		return this.device;
	}
	
	public String getMountPoint() {
		return this.mountPoint;
	}
	
	public String getFsType() {
		return this.fsType;
	}
}
