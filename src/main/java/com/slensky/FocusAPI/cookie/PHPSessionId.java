package com.slensky.FocusAPI.cookie;

import com.slensky.FocusAPI.util.Constants;

public class PHPSessionId {
   
   private final String name = Constants.PHPSESSID;
   private String content;
   
   public void setContent(String content) {
      this.content = content;
   }
   
   public String getName() {
      return name;
   }
   public String getContent() {
      return content;
   }
   
   public String toString() {
      return name + "=" + content;
   }
   
}
