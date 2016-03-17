package com.slensky.FocusAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.cookie.CurrentSession;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.studentinfo.Course;
import com.slensky.FocusAPI.studentinfo.CourseAssignments;
import com.slensky.FocusAPI.studentinfo.FinalExam;
import com.slensky.FocusAPI.studentinfo.FinalGrade;
import com.slensky.FocusAPI.studentinfo.GraduationRequirement;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.studentinfo.SchoolEvent;
import com.slensky.FocusAPI.studentinfo.StudentInformation;
import com.slensky.FocusAPI.util.Constants;
import com.slensky.FocusAPI.util.Logger;
import com.slensky.FocusAPI.util.URLRetriever;
import com.slensky.FocusAPI.util.Util;

public final class Focus {
   
   public enum School {
      ASD
   }
   
   private final String user;
   private final String pass;
   private final School school;
   private PHPSessionId phpSessId = new PHPSessionId();
   private CurrentSession currentSession = new CurrentSession();
   private List<MarkingPeriod> markingPeriods = new ArrayList<MarkingPeriod>();
   private MarkingPeriod currentMarkingPeriod;
   
   public Focus(String user, String pass, School school) throws FailedLoginException, IOException {
      this.user = user;
      this.pass = pass;
      this.school = school;
      URLRetriever.setSchool(school);
      long start = System.currentTimeMillis();
      if (!logIn()) {
         throw new FailedLoginException();
      }
      if (!currentMarkingPeriod.equals(getMostRecentMarkingPeriod())) {
         Logger.log("Marking period incorrect, changing...");
         changeMarkingPeriod(getMostRecentMarkingPeriod());
      }
      Logger.log("Login sequence finished (" + (System.currentTimeMillis() - start) + " ms)");
   }
   
   private boolean logIn() throws IOException {
      
      long start = System.currentTimeMillis();
      
      Connection.Response response = null;
      Document login = null;
      
      response = Jsoup.connect(URLRetriever.getLoginURL())
            .data("login", "true", "data", "username=" + user + "&password=" + pass)
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
      login = response.parse();
      
      if (login.toString().indexOf("\"success\":true") >= 0) {
         Logger.log("Login successful (" + (System.currentTimeMillis() - start) + " ms)");
      }
      else {
         Logger.log("Login unsuccessful");
         return false;
      }
      
      phpSessId.setContent(response.cookie(phpSessId.getName()));
      Logger.log(phpSessId.toString());
   
      start = System.currentTimeMillis();
      
      Document portal = null;
      portal = Jsoup.connect(URLRetriever.getTLD())
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .timeout(Constants.CONNECTION_TIMEOUT)
            .get();
      String portalStr = portal.html();
      
      int cookieStart = portalStr.indexOf("var session = ") + 14;
      int cookieEnd = portalStr.indexOf("Cookies.set('current_session', JSON") - 3;
      currentSession.parseJSONFields(portalStr.substring(cookieStart, cookieEnd));
      
      int markingPeriodId = Integer.parseInt(
            portal.getElementsByAttributeValue("name", "side_mp").get(0) //get the container for the marking period options
            .getElementsByAttribute("selected").get(0).attr("value")); //get the value of the selected option
      
      markingPeriods = getMarkingPeriods();
      for (MarkingPeriod mp : markingPeriods) {
         if (mp.getMarkingPeriodId() == markingPeriodId) {
            currentMarkingPeriod = mp;
         }
      }
      
      return true;
   
   }
   
   // changes the marking period
   private void changeMarkingPeriod(MarkingPeriod markingPeriod) throws IOException {
      currentSession.setYear(Integer.toString(markingPeriod.getYear()));
      currentSession.setMarkingPeriod(Integer.toString(markingPeriod.getMarkingPeriodId()));
      currentMarkingPeriod = markingPeriod;
      Util.setMarkingPeriod(markingPeriod, phpSessId);
   }
   //returns a list of all marking periods
   public List<MarkingPeriod> getMarkingPeriods() throws IOException {
      ensureMarkingPeriodsDownloaded();
      return markingPeriods;
   }
   //gets the most recent marking period
   public MarkingPeriod getMostRecentMarkingPeriod() throws IOException {
      ensureMarkingPeriodsDownloaded();
      MarkingPeriod recent = null;
      for (MarkingPeriod mp : markingPeriods) {
         if (recent == null) {
            recent = mp;
         }
         else if (mp.getYear() > recent.getYear()) {
            recent = mp;
         }
         else if ((mp.getYear() == recent.getYear()) &&
               mp.getTerm().ordinal() > recent.getTerm().ordinal()) {
            recent = mp;
         }
         
      }
      return recent;
   }
   
   //makes sure the marking periods have been downloaded for purposes of retrieving them
   private void ensureMarkingPeriodsDownloaded() throws IOException {
      if (markingPeriods.isEmpty()) {
         try {
            URL jsonURL = new URL(URLRetriever.getMarkingPeriodURL());
            String jsonStr = IOUtils.toString(jsonURL);
            
            JSONArray jsonMarkingPeriods = new JSONObject(jsonStr).getJSONArray("Marking Periods");
            for (int i = 0; i < jsonMarkingPeriods.length(); i++) {
               JSONObject current = jsonMarkingPeriods.getJSONObject(i);
               int year = current.getInt("year");
               MarkingPeriod.Term term = MarkingPeriod.Term.valueOf(current.getString("type"));
               int id = current.getInt("id");
               markingPeriods.add(new MarkingPeriod(year, term, id));
            }
         } catch (MalformedURLException e) {
            //this should never ever ever happen
            e.printStackTrace();
         }
      }
         
   }
   //gets the current marking period
   public MarkingPeriod getCurrentMarkingPeriod() {
      return currentMarkingPeriod;
   }
   
   public List<Course> getCourses(MarkingPeriod markingPeriod) {return null;}
   public Map<Course, CourseAssignments> getCourseAssignments(MarkingPeriod markingPeriod) {return null;}
   public List<FinalGrade> getFinalGrades() {return null;}
   public List<FinalExam> getFinalExams() {return null;}
   public List<GraduationRequirement> getGraduationRequrements() {return null;}
   public List<SchoolEvent> getEventsFromCalendar(int yearStart, int yearEnd) {return null;}
   public StudentInformation getStudentInformation(MarkingPeriod markingPeriod) {return null;}
   
}
