package com.slensky.FocusAPI.util;

import java.util.HashMap;
import java.util.Map;

import com.slensky.FocusAPI.Focus;

public class URLRetriever {
   
   private static Map<Focus.School, String> FocusTLDs = new HashMap<Focus.School, String>();
   private static Map<Focus.School, String> FocusLoginURLs = new HashMap<Focus.School, String>();
   private static Map<Focus.School, String> FocusPortalURLs = new HashMap<Focus.School, String>();
   private static Map<Focus.School, String> MarkingPeriodURLs = new HashMap<Focus.School, String>();
   
   private static Focus.School school;
   
   static {
      FocusTLDs.put(Focus.School.ASD, "https://focus.asdnh.org/focus/");
      FocusLoginURLs.put(Focus.School.ASD, "index.php");
      FocusPortalURLs.put(Focus.School.ASD, "Modules.php?modname=misc/Portal.php");
      MarkingPeriodURLs.put(Focus.School.ASD, "https://raw.githubusercontent.com/virtigo/FocusAPI/master/school/asd/MARKING_PERIODS.json");
   }
   
   public static void setSchool(Focus.School school) {
      URLRetriever.school = school;
   }
   
   public static String getTLD() {
      return FocusTLDs.get(school);
   }
   public static String getLoginURL() {
      return FocusTLDs.get(school) + FocusLoginURLs.get(school);
   }
   public static String getPortalURL() {
      return FocusTLDs.get(school) + FocusPortalURLs.get(school);
   }
   public static String getMarkingPeriodURL() {
      return MarkingPeriodURLs.get(school);
   }
   
}
