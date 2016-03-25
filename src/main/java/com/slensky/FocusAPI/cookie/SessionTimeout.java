package com.slensky.FocusAPI.cookie;

public class SessionTimeout implements Cookie {

   public static final int TIME_LIMIT = 1440 * 1000;
   
   private final String name = "session_timeout";
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
