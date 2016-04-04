package com.slensky.FocusAPI.util;

import com.slensky.FocusAPI.Focus;

public class Logger {
   
   public static enum logType {
      error_severe,
      error,
      warning,
      info
   }
  
   public static void log(String s) {
      log(s, logType.info);
   }
   
   public static void log(String s, logType t) {
      if (Focus.getOptions().getLogging()) {
         String output = "";
         switch (t) {
             case error_severe:
                 output = "[ERROR-CRITICAL] ";
                 break;
             case error:
                 output = "[ERROR] ";
                 break;
             case warning:
                 output = "[WARNING] ";
                 break;
             case info:
                 output = "[INFO] ";
                 break;
         }
         output = output + s;
         System.out.println(output);
      }
  }
  
}
