package com.gpsonguard.util;

import java.util.Vector;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import 	java.text.SimpleDateFormat;

public class SharedData {

	private static Vector contactsVector;
	private static Vector guardMetaInfo;
	private static Vector guardPoiVector;
	private static String siteId;
	private static String guardId;
	private static String messageNumber;
	private static Vector guardSiteInformation;
	private static SimpleDateFormat dateFormat;
	private static SimpleDateFormat dateF;
	private static int siteVectorValue = -1;
	public static boolean supervisor = false;
	private static SimpleDateFormat homeScreenDateFormat;
	private static String secretCode;
	
	private static double currentLatitude;
	private static double currentLongitude;

	
	public static boolean isSupervisor() {
		return supervisor;
	}
	
	public static String getSecretCode () {
		return secretCode;
	}
	
	public static void setSecretCode(String guardID) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			String finalSecret = guardID.trim() + "d9b4e030600574";
			md.update(finalSecret.getBytes()); 
			byte[] output = md.digest();	
			secretCode = bytesToHex(output);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void setSupervisor(boolean sup) {
		supervisor = sup;
	}

	public static void setSiteVectorValue(int siteVectorValue) {
		SharedData.siteVectorValue = siteVectorValue;
	}

	public static int getSiteVectorValue() {
		return siteVectorValue;
	}

	public static void setSiteId(String siteId) {
		SharedData.siteId = siteId;
	}

	public static String getSiteId() {
		return siteId;
	}

	public static void setMessageNumber(String messageNumber) {
		SharedData.messageNumber = messageNumber;
	}

	public static String getMessageNumber() {
		return messageNumber;
	}

	public static Vector getContactVectorInstance() {

		if (contactsVector == null) {
			contactsVector = new Vector();
		}
		return contactsVector;

	}

	public static Vector getGuardMetaInfoInstance() {

		if (guardMetaInfo == null) {
			guardMetaInfo = new Vector();
		}
		return guardMetaInfo;

	}

	public static Vector getGuardPoiVectorInstance() {

		if (guardPoiVector == null) {
			guardPoiVector = new Vector();
		}
		return guardPoiVector;

	}

	public static void setGuardId(String guardId) {
		SharedData.guardId = guardId;
	}

	public static String getGuardId() {
		return guardId;
	}

	public static SimpleDateFormat getSimpleDateFormat() {
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return dateFormat;
	}
	
	public static SimpleDateFormat getSimpleDate() {
		if (dateF == null) {
			dateF = new SimpleDateFormat("yyyy-MM-dd");
		}
		
		return dateF;
	}

	public static Vector getGuardSiteInfoVectorInstance() {
		if (guardSiteInformation == null) {
			guardSiteInformation = new Vector();
		}
		return guardSiteInformation;
	}

	public static SimpleDateFormat getHomeScreenDateFormat() {

		if (homeScreenDateFormat == null) {
			homeScreenDateFormat = new SimpleDateFormat("hh:mm a");
		}
		return homeScreenDateFormat;

	}

	public static double getCurrentLatitude() {
		return currentLatitude;
	}

	public static void setCurrentLatitude(double currentLatitude) {
		SharedData.currentLatitude = currentLatitude;
	}

	public static double getCurrentLongitude() {
		return currentLongitude;
	}

	public static void setCurrentLongitude(double currentLongitude) {
		SharedData.currentLongitude = currentLongitude;
	}
	
	public static String bytesToHex(byte[] b) {
	      char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
	                         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	      StringBuffer buf = new StringBuffer();
	      for (int j=0; j<b.length; j++) {
	         buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
	         buf.append(hexDigit[b[j] & 0x0f]);
	      }
	      return buf.toString();
	  }
	
}
