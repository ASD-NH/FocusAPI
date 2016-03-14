package com.slensky.FocusAPI;

import java.io.IOException;
import java.util.*;

import javax.security.auth.login.FailedLoginException;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.slensky.FocusAPI.cookie.*;
import com.slensky.FocusAPI.studentinfo.*;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod.Term;
import com.slensky.FocusAPI.util.*;

public final class Focus {
   
   private final String user;
   private final String pass;
   private PHPSessionId phpSessId = new PHPSessionId();
   private CurrentSession currentSession = new CurrentSession();
   private MarkingPeriod currentMarkingPeriod;
   
   public Focus(String user, String pass) throws FailedLoginException, IOException {
      this.user = user;
      this.pass = pass;
      long start = System.currentTimeMillis();
      if (!logIn()) {
         throw new FailedLoginException();
      }
      currentMarkingPeriod = getMostRecentMarkingPeriod();
      changeMarkingPeriod(currentMarkingPeriod);
      System.out.println("done (" + (System.currentTimeMillis() - start) + " ms)");
   }
   
   private boolean logIn() throws IOException {
      
      Connection.Response response = null;
      Document login = null;
      
      response = Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_LOGIN_URL)
            .data("login", "true", "data", "username=" + user + "&password=" + pass)
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
      login = response.parse();
      
      if (login.toString().indexOf("\"success\":true") >= 0) {
         Logger.log("Login successful");
      }
      else {
         Logger.log("Login unsuccessful");
         return false;
      }
      
      phpSessId.setContent(response.cookie(Constants.PHPSESSID));
      Logger.log(phpSessId.toString());
   
      String portal = null;
      portal = Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .timeout(Constants.CONNECTION_TIMEOUT)
            .get()
            .html();
      
      int cookieStart = portal.indexOf("var session = ") + 14;
      int cookieEnd = portal.indexOf("Cookies.set('current_session', JSON") - 3;
      currentSession.parseJSONFields(portal.substring(cookieStart, cookieEnd));
      Logger.log("Current Session: " + currentSession.getEncodedContent());
      
      return true;
   
   }
   
   // changes the marking period
   private void changeMarkingPeriod(MarkingPeriod markingPeriod) throws IOException {
      currentSession.setYear(Integer.toString(markingPeriod.getYear()));
      currentSession.setMarkingPeriod(Integer.toString(markingPeriod.getMarkingPeriodId()));
      currentMarkingPeriod = markingPeriod;
      
      //submit request to change years
      Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .data(currentSession.asFormData())
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
   }
   
   //returns a list of marking periods for the given year
   public List<MarkingPeriod> getMarkingPeriodsForYear(int year) throws IOException {
      
      //ensure the year is within reasonable bounds
      if (year < 2007 || year > Calendar.getInstance().get(Calendar.YEAR)) {
         throw new Error("Year out of bounds");
      }

      //return this
      List<MarkingPeriod> markingPeriods = new ArrayList<MarkingPeriod>();
      //used for resetting after the function finishes
      String originalYear = currentSession.getYear();
      
      //set the session cookie to the desired year
      currentSession.setYear(Integer.toString(year));
      
      //submit request to change years
      Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .data(currentSession.asFormData())
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
      
      //retrieve marking period elements
      Elements markingPeriodElements = Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .timeout(Constants.CONNECTION_TIMEOUT)
            .get().getElementsByAttributeValue("name", "side_mp").get(0).getElementsByTag("option");
      
      for (Element e : markingPeriodElements) {
         Term term = Util.parseTerm(e.ownText());
         int markingPeriodId = Integer.parseInt(e.attr("value"));
         markingPeriods.add(new MarkingPeriod(year, term, markingPeriodId));
      }
      
      //reset
      currentSession.setYear(originalYear);
      Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .data(currentSession.asFormData())
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
      
      for (MarkingPeriod m : markingPeriods) {
         System.out.println(m.getYear() + "   " + m.getTerm() + "   " + m.getMarkingPeriodId());
      }
      
      return markingPeriods;
      
   }
   
   //gets the most recent marking period
   public MarkingPeriod getMostRecentMarkingPeriod() throws IOException {
      String originalYear = currentSession.getYear();
      int syear;
      Calendar currentDate = Calendar.getInstance();
      if (currentDate.get(Calendar.MONTH) < Calendar.AUGUST) {
         syear = currentDate.get(Calendar.YEAR) - 1;
      }
      else {
         syear = currentDate.get(Calendar.YEAR);
      }
      currentSession.setYear(Integer.toString(syear));
      
      //submit request to change years
      Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .data(currentSession.asFormData())
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
      
      //retrieve marking period elements
      Elements markingPeriodElements = Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .timeout(Constants.CONNECTION_TIMEOUT)
            .get().getElementsByAttributeValue("name", "side_mp").get(0).getElementsByTag("option");
      
      MarkingPeriod mostRecent = null;
      for (Element e : markingPeriodElements) {
         Term term = Util.parseTerm(e.ownText());
         int markingPeriodId = Integer.parseInt(e.attr("value"));
         MarkingPeriod current = new MarkingPeriod(syear, term, markingPeriodId);
         if (mostRecent == null) {
            mostRecent = current;
         }
         else {
            if (current.getTerm().ordinal() > mostRecent.getTerm().ordinal()) {
               mostRecent = current;
            }
         }
      }
      
      //reset
      currentSession.setYear(originalYear);
      Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
            .cookie(phpSessId.getName(), phpSessId.getContent())
            .data(currentSession.asFormData())
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
      
      return mostRecent;
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
