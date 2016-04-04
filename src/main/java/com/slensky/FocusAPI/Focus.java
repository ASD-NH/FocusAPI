package com.slensky.FocusAPI;

import java.io.IOException;

import javax.security.auth.login.FailedLoginException;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.cookie.CurrentSession;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.cookie.SessionTimeout;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.util.Logger;
import com.slensky.FocusAPI.util.URLRetriever;

/**
 * Controller class for all things Focus. Encapsulates the downloader, student information, cookies, and school
 * @author Stephan Lensky
 */
public class Focus {
   
   public enum School {
      ASD
   }
   
   private final String user;
   private final String pass;
   //possibly unneeded? comment back in if that changes
   //private final School school;
   private final PHPSessionId sessId = new PHPSessionId();
   private final CurrentSession currSess = new CurrentSession();
   private final SessionTimeout sessTimeout = new SessionTimeout();
   private final StudentInfo studentInfo;
   
   private static final FocusOptions options = new FocusOptions();
   private final FocusDownloader downloader;
   
   /**
    * Constructs a new Focus object, logs in, and ensures that 
    * we're set to the most recent marking period
    * @param user the user's username
    * @param pass the user's password
    * @param school the user's school
    * @throws FailedLoginException if the login failed due to bad credentials
    * @throws IOException if the connection times out
    */
   public Focus(String user, String pass, School school) throws FailedLoginException, IOException {
      this.user = user;
      this.pass = pass;
      //this.school = school;
      
      //config
      URLRetriever.setSchool(school);
      
      //init
      downloader = new FocusDownloader(this);
      studentInfo = new StudentInfo(this);
      
      //timer
      long start = System.currentTimeMillis();
      
      //test if the client gave us good login info and log in
      if (!logIn()) {
         throw new FailedLoginException("Username/Password incorrect");
      }
      
      //fix marking period if necessary
      if (!studentInfo.getCurrentMarkingPeriod().equals(studentInfo.getMostRecentMarkingPeriod())) {
         Logger.log("Marking period incorrect, changing...");
         
         try {
            studentInfo.changeMarkingPeriod(studentInfo.getMostRecentMarkingPeriod());
         } catch(SessionExpiredException e) {
            // This will only happen if 24 minutes pass between the top of this constructor and here
            // Pretty unlikely, so just print out a stacktrace and hope for the best
            e.printStackTrace();
         }
         
      }
      
      Logger.log("Login sequence finished (" + (System.currentTimeMillis() - start) + " ms)");
      
   }
   
   /**
    * Logs in and sets the current marking period. Call this if the session times out
    * @return whether or not the login was successful
    * @throws IOException
    */
   public boolean logIn() throws IOException {
      
      boolean success = sendLoginRequest();
      if (!success) {
         return false;
      }
      
      Connection.Response portalResp = Jsoup.connect(URLRetriever.getTLD())
            .cookie(sessId.getName(), sessId.getContent())
            .method(Method.GET)
            .timeout(options.getTimeout())
            .execute();
      
      sessTimeout.setContent(portalResp.cookies().get("session_timeout"));
      Logger.log(sessTimeout.toString());
      
      long time = System.currentTimeMillis();
      long timeDifference = Integer.parseInt(sessTimeout.getContent()) - time / 1000;
      downloader.setSessionExpiration(time + SessionTimeout.TIME_LIMIT + timeDifference);
      
      Document portal = portalResp.parse();
      String portalStr = portal.html();
      
      int cookieStart = portalStr.indexOf("var session = ") + 14;
      int cookieEnd = portalStr.indexOf("Cookies.set('current_session', JSON") - 3;
      currSess.parseJSONFields(portalStr.substring(cookieStart, cookieEnd));
      
      int markingPeriodId = Integer.parseInt(
            portal.getElementsByAttributeValue("name", "side_mp").get(0) //get the container for the marking period options
            .getElementsByAttribute("selected").get(0).attr("value")); //get the value of the selected option
      
      for (MarkingPeriod mp : studentInfo.getMarkingPeriods()) {
         if (mp.getMarkingPeriodId() == markingPeriodId) {
            studentInfo.setCurrentMarkingPeriod(mp);
            downloader.getPortal().cache(studentInfo.getCurrentMarkingPeriod(), portal, false);
         }
      }
      
      return true;
   
   }
   
   private boolean sendLoginRequest() throws IOException {
      Connection.Response response = null;
      Document login = null;
      
      response = Jsoup.connect(URLRetriever.getLoginURL())
            .data("login", "true", "data", "username=" + user + "&password=" + pass)
            .method(Method.POST)
            .timeout(options.getTimeout())
            .execute();
      login = response.parse();
      
      if (login.toString().indexOf("\"success\":true") >= 0) {
         Logger.log("Login successful");
         sessId.setContent(response.cookie(sessId.getName()));
         Logger.log(sessId.toString());
         return true;
      }
      else {
         Logger.log("Login unsuccessful");
         return false;
      }
   }
   
   /**
    * Gets the session ID cookie
    * @return the PHPSESSIONID cookie
    */
   public PHPSessionId getSessId() {
      return sessId;
   }
   
   /**
    * Gets the current sesion cookie
    * @return the current_session cookie
    */
   public CurrentSession getCurrSess() {
      return currSess;
   }
   
   /**
    * Gets the session timeout cookie
    * @return the sessiontimeout cookie
    */
   public SessionTimeout getSessTimeout() {
      return sessTimeout;
   }
   
   /**
    * Gets the object that controls all information related to the student
    * @return the StudentInfo object for this Focus instance
    */
   public StudentInfo getStudentInfo() {
      return studentInfo;
   }
   /**
    * Gets the options
    * @return the options for all Focus instances
    */
   public static FocusOptions getOptions() {
      return options;
   }
   /**
    * Gets the object that controls downloading all webpages that are scraped by the API
    * @return the FocusDownloader object for this Focus instance
    */
   public FocusDownloader getDownloader() {
      return downloader;
   }
   
}
