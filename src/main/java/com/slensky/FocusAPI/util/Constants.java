package com.slensky.FocusAPI.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Constants {
   
   public static final URL FOCUS_TLD;
   public static final URL FOCUS_URL;
   public static final URL FOCUS_PORTAL_URL;
   public static final URL FOCUS_SCHEDULE_URL;

   static {
      URL tempTLD;
      URL tempFocus;
      URL tempPortal;
      URL tempSchedule;
      try {
         tempFocus = new URL("https://focus.asdnh.org/focus/index.php");
         tempPortal = new URL("https://focus.asdnh.org/focus/Modules.php?modname=misc/Portal.php");
         tempTLD = new URL("https://focus.asdnh.org");
         tempSchedule = new URL("https://focus.asdnh.org/focus/Modules.php?modname=Scheduling/Schedule.php");
      } catch (MalformedURLException e) {
         tempFocus = null;
         tempPortal = null;
         tempTLD = null;
         tempSchedule = null;
      }
      FOCUS_TLD = tempTLD;
      FOCUS_URL = tempFocus;
      FOCUS_PORTAL_URL = tempPortal;
      FOCUS_SCHEDULE_URL = tempSchedule;
   }
   
   public static final boolean debugLogging = true;
   public static final String PHPSESSID = "PHPSESSID";
   
   public enum day {
      MONDAY,
      TUESDAY,
      WEDNESDAY,
      THURSDAY,
      FRIDAY
   }
   public enum termLength {
      FULL_YEAR
   }
   
}
