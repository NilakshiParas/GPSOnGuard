package com.gpsonguard.util;

public class Constants {

    public static final String GPSONGuardPrefrances = "gps_prefrances";
    public static final String GUARD_ID = "guard_id";


    public static final int LARGEWIDTH = 480;
    public static final int MEDIUMWIDTH = 360;
    public static final int SMALLWIDTH = 320;
    public static final int LARGEHEIGHT = 360;
    public static final int MEDIUMHEIGHT = 480;
    public static final int SMALLHEIGHT = 240;
    //public static final int SCREENWIDTH = Display.getWidth();
    //public static final int SCREENHEIGHT = Display.getHeight();

    public static final String WEB_SERVER = "https://www.gpsonguard.com/devices/";
    //public static final String WEB_SERVER = "http://new.gpsonguard.com/devices/";

    //public static final String WEB_SERVER = "http://192.168.2.11:8081/devices/";

    public static final String LOGIN_URL = "get_guard_auth";
    public static final String REAUTH_LOGIN_URL = "get_guard_auth/reauth";
    public static final String GUARD_PROFILE_INFO_URL = "get_guard_info";
    public static final String EMERGENCY_NUMBER_URL = "get_emergency_contacts";
    public static final String LOGOUT_URL = "get_guard_auth";
    public static final String SCAN_QRCODE_URL = "send_qrcode_info";
    public static final String LAT_LONG_INFO_URL = "send_guard_location";
    public static final String IMAGE_POST_URL = "send_image_data";
    public static final String GUARD_PATROL_POINTS = "get_site_poi";
    public static final String STANDING_ORDER = "get_standing_order";
    public static final String SEND_MESSAGE_URL = "send_message";
    public static final String FETCH_MESSAGE_URL = "fetch_messages";
    public static final String READ_MESSAGE_URL = "message_read";
    public static final String SEND_RED_MESSAGE_URL = "send_emergency_message";
    public static final String ADD_POI_URL = "send_new_poi";

}
