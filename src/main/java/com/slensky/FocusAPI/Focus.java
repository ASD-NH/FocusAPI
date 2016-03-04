package com.slensky.FocusAPI;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.extract.Extractor;
import com.slensky.FocusAPI.struct.Course;
import com.slensky.FocusAPI.struct.PageIndex;
import com.slensky.FocusAPI.util.Constants;
import com.slensky.FocusAPI.util.Logger;

public class Focus {
   
   PageIndex pageIndex;
   
   String user;
   String pass;
   String phpSessId;
   
   public Focus() {}
   
   public boolean logIn(String user, String pass) {
      
      this.user = user;
      this.pass = pass;
      
      try {
         
         Connection.Response response = Jsoup.connect(Constants.FOCUS_URL.toString())
               .data("login", "true", "data", "username=" + user + "&password=" + pass)
               .method(Method.POST)
               .execute();
         
         Document login = response.parse();
         if (login.toString().indexOf("\"success\":true") >= 0) {
            Logger.log("Login successful");
         }
         else {
            Logger.log("Login unsuccessful");
            return false;
         }
         
         phpSessId = response.cookie(Constants.PHPSESSID);
         pageIndex = new PageIndex(phpSessId);
         Logger.log(phpSessId);
         
      } catch (Exception e) {e.printStackTrace(System.out);}
      
      return true;
   }
   
   public ArrayList<Course> getClasses() throws IOException {
      return Extractor.extractCourses(pageIndex);
   }
   
}
