package com.slensky.FocusAPI;

import com.slensky.FocusAPI.util.Constants;

public class FocusOptions {
   
   private int timeout = Constants.CONNECTION_TIMEOUT;
   
   public void setTimeout(int ms) {
      timeout = ms;
   }
   int getTimeout() {
      return timeout;
   }
   
}
