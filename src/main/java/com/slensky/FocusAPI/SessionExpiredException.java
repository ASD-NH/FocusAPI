package com.slensky.FocusAPI;

import javax.security.auth.login.LoginException;

public class SessionExpiredException extends LoginException {
   
   public SessionExpiredException() {
      super();
   }
   public SessionExpiredException(String message) {
      super(message);
   }
   
}
