package com.slensky.FocusAPI.util;

public class Constants {
   
   public static final String FOCUS_TLD = "https://focus.asdnh.org/focus/";
   public static final String FOCUS_LOGIN_URL = "index.php";
   public static final String FOCUS_PORTAL_URL = "Modules.php?modname=misc/Portal.php";
   
   public static final int CONNECTION_TIMEOUT = 10 * 1000;
   
   public static final boolean debugLogging = true;
   
   public static final String PHPSESSID = "PHPSESSID";
   public static final String CURRENT_SESSION = "current_session";
   
   public enum day {
      MONDAY,
      TUESDAY,
      WEDNESDAY,
      THURSDAY,
      FRIDAY
   }
   
}
