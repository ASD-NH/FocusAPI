package com.slensky.FocusAPI;

import javax.security.auth.login.LoginException;

public class SessionExpiredException extends LoginException {
   
   private static final long serialVersionUID = 1614401813414001708L;
   public SessionExpiredException() {
      super();
   }
   public SessionExpiredException(String message) {
      super(message);
   }
   
}
