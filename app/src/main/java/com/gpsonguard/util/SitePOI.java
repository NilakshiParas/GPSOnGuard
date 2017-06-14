package com.gpsonguard.util;

public class SitePOI {
	
	private double latitude;
	private double longitude;
	private String poiName;
	private String siteID;
	private String guardID;
	private long lastChecked;
	
	private double radius = 5.0;

	public double getLatitude() {
		return latitude;		
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getPoiName() {
		return poiName;
	}

	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}

	public String getSiteID() {
		return siteID;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public String getGuardID() {
		return guardID;
	}

	public void setGuardID(String guardID) {
		this.guardID = guardID;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public long getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(long lastChecked) {
		this.lastChecked = lastChecked;
	}
	
	
	
	

}
