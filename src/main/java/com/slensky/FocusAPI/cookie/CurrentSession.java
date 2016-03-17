package com.slensky.FocusAPI.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;


import com.slensky.FocusAPI.util.Constants;

public class CurrentSession implements Cookie {
   
   private String name = "current_session";
   private Map<String, String> fields = new LinkedHashMap<String, String>();
   
   public void parseJSONFields(String json) {
      json = json.substring(1, json.length() - 1);
      String fields[] = json.split(",");
      for (String f : fields) {
         f = f.replace("\"", "");
         String name = f.split(":")[0];
         String data = f.split(":")[1];
         this.fields.put(name, data);
      }
   }
   
   
   public String getName() {
      return name;
   }
   public String getContent() {
      //build json
      String content = "{";
      for (String k : fields.keySet()) {
         content = content + "\"" + k + "\":\"" + fields.get(k) + "\"" + ",";
         content = content.replace("\"null\"", "null");
      }
      content = content.substring(0, content.length() - 1);
      content = content + "}";
      
      //encode
      content = content.replace("\"", "%22").replace(",", "%2C");
      
      return content;
   }
   public Collection<org.jsoup.Connection.KeyVal> asFormData() {
      Collection<org.jsoup.Connection.KeyVal> formData = new HashSet<org.jsoup.Connection.KeyVal>();
      formData.add(org.jsoup.helper.HttpConnection.KeyVal.create("side_syear", getYear()));
      formData.add(org.jsoup.helper.HttpConnection.KeyVal.create("side_mp", getMarkingPeriod()));
      return formData;
   }
   public String getMarkingPeriod() {
      return fields.get("UserMP");
   }
   public void setMarkingPeriod(String mp) {
      fields.put("UserMP", mp);
   }
   public String getYear() {
      return fields.get("UserSyear");
   }
   public void setYear(String y) {
      fields.put("UserSyear", y);
   }
   
}
