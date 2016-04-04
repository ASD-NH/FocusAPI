package com.slensky.FocusAPI.cookie;

public class PHPSessionId implements Cookie {
   
   private final String name = "PHPSESSID";
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
