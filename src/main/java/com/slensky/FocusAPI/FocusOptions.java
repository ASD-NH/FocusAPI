package com.slensky.FocusAPI;

public class FocusOptions {
   
   private static final int DEFAULT_TIMEOUT = 6 * 1000;
   private int timeout = DEFAULT_TIMEOUT;
   public void setTimeout(int ms) {
      timeout = ms;
   }
   public void restoreDefaultTimeout() {
      timeout = DEFAULT_TIMEOUT;
   }
   int getTimeout() {
      return timeout;
   }
   
   private static final boolean DEFAULT_LOGGING = true;
   private boolean logging = DEFAULT_LOGGING;
   public void setLogging(boolean logging) {
      this.logging = logging;
   }
   public void restoreDefaultLogging() {
      logging = DEFAULT_LOGGING;
   }
   public boolean getLogging() {
      return logging;
   }
   
}
