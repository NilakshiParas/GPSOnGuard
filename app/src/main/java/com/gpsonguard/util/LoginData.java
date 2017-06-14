package com.gpsonguard.util;

import java.util.Vector;

public class LoginData {
	
	private static String guardName;
	
	public static String getGuardName() {
		return guardName;
	}
	public static void setGuardName(String guardName) {
		LoginData.guardName = guardName;
	}
	public static String getStartTime() {
		return startTime;
	}
	public static void setStartTime(String startTime) {
		LoginData.startTime = startTime;
	}
	public static String getEndTime() {
		return endTime;
	}
	public static void setEndTime(String endTime) {
		LoginData.endTime = endTime;
	}
	public static String getCompanyID() {
		return companyID;
	}
	public static void setCompanyID(String companyID) {
		LoginData.companyID = companyID;
	}
	public static String getSiteId() {
		return siteId;
	}
	public static void setSiteId(String siteId) {
		LoginData.siteId = siteId;
	}
	public static String getGuardId() {
		return guardId;
	}
	public static void setGuardId(String guardId) {
		LoginData.guardId = guardId;
	}
	public static boolean getGuardStatus() {
		return guardStatus;
	}
	public static void setGuardStatus(boolean guardStatus) {
		LoginData.guardStatus = guardStatus;
	}
	
	private static String startTime;
	private static String endTime;
	private static String companyID;
	private static String siteId;
	private static String guardId;
	private static boolean guardStatus = false;
	private static String supervisor = "N";

	public static String getSupervisor() {
		return supervisor;
	}
	public static void setSupervisor(String supervisor) {
		LoginData.supervisor = supervisor;
	}
	
	
	

}
