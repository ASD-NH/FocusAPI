package com.slensky.FocusAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.slensky.FocusAPI.cookie.CurrentSession;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.studentinfo.Course;
import com.slensky.FocusAPI.studentinfo.CourseAssignments;
import com.slensky.FocusAPI.studentinfo.FinalExam;
import com.slensky.FocusAPI.studentinfo.FinalGrade;
import com.slensky.FocusAPI.studentinfo.GraduationRequirement;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.studentinfo.PortalInfo;
import com.slensky.FocusAPI.studentinfo.SchoolEvent;
import com.slensky.FocusAPI.studentinfo.StudentInformation;
import com.slensky.FocusAPI.util.Constants;
import com.slensky.FocusAPI.util.URLRetriever;
import com.slensky.FocusAPI.util.Util;

public class StudentInfo {
   
   private final Focus focus;
   private final FocusDownloader downloader;
   
   private MarkingPeriod currentMarkingPeriod;
   
   private List<MarkingPeriod> markingPeriods = new ArrayList<MarkingPeriod>();
   private PortalInfo portalInfo;
   
   public StudentInfo(Focus focus) {
      this.focus = focus;
      this.downloader = focus.getDownloader();
   }
   
   /*
    * Retrieve/set things related to student info
    */
   
   // changes the marking period
   public void changeMarkingPeriod(MarkingPeriod markingPeriod) throws IOException, SessionExpiredException {
      downloader.setMarkingPeriod(markingPeriod);
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
   //no access modifier to limit access to the package and not expose it when distributing the API
   void setCurrentMarkingPeriod(MarkingPeriod mp) {
      currentMarkingPeriod = mp;
   }
   //makes sure the marking periods have been downloaded for purposes of retrieving them
   private void ensureMarkingPeriodsDownloaded() throws IOException {
      if (markingPeriods.isEmpty()) {
         try {
            String jsonStr = downloader.getMarkingPeriodJSON();
            
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
   
   public PortalInfo getPortalInfo(MarkingPeriod mp) throws SessionExpiredException, IOException {
      if (portalInfo == null) {
         
         Document portal = focus.getDownloader().getPortal(mp);
         Elements courseLinks = portal.getElementsByAttributeValueStarting("href", "Modules.php?modname=Grades/StudentGBGrades.php?course_period_id=");
         
         Elements courseNames = new Elements();
         Elements courseGrades = new Elements();
         
         for (Element e : courseLinks) {
            //weed out duplicate elements (multiple mentions of courses etc.
            if (!(e.html().indexOf("›") >= 0 || e.html().indexOf("<b>") >= 0)) {
               //sort by if the elements by name/grade
               if (e.html().indexOf('%') >= 0) {
                  courseGrades.add(e);
               }
               else {
                  courseNames.add(e);
               }
            }
         }
         assert(courseNames.size() == courseGrades.size());
         
         List<Course> courses = new ArrayList<Course>();
         List<Integer> grades = new ArrayList<Integer>();
         
         for (Element e : courseNames) {
            String t = e.text();
            
            String name, teacher;
            int period;
            List<DayOfWeek> meetingDays = new ArrayList<DayOfWeek>();
            
            String[] courseInfo = t.split(" - ");
            name = courseInfo[0];
            teacher = courseInfo[4];
            
            String periodStr = courseInfo[1].substring(courseInfo[1].length() - 1, courseInfo[1].length());
            period = Integer.parseInt(periodStr);
            
            for (char d : courseInfo[2].toLowerCase().toCharArray()) {
               switch (d) {
                  case 'm':
                     meetingDays.add(DayOfWeek.MONDAY);
                     break;
                  case 't':
                     meetingDays.add(DayOfWeek.TUESDAY);
                     break;
                  case 'w':
                     meetingDays.add(DayOfWeek.WEDNESDAY);
                     break;
                  case 'h':
                     meetingDays.add(DayOfWeek.THURSDAY);
                     break;
                  case 'f':
                     meetingDays.add(DayOfWeek.FRIDAY);
                     break;
               }
            }
            
            courses.add(new Course(period, name, teacher, meetingDays, null, mp));
            
         }
         for (Element e : courseGrades) {
            String t = e.text();
            t = t.substring(0, t.indexOf('%'));
            grades.add(Integer.parseInt(t));
         }
         
         List<SchoolEvent> upcomingEvents = new ArrayList<SchoolEvent>();
         Elements events = portal.getElementsByAttributeValueStarting("onclick", "switchMenu(\"calendar");
         
         for (Element e : events) {
            String t = e.text();
            
            String date = t.substring(0, t.indexOf(": "));
            Calendar eventDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            try {
               eventDate.setTime(dateFormat.parse(date));
            } catch (ParseException e1) {
               //should never happen, we do a unit test for this
               e1.printStackTrace();
            }
            
            String name = t.substring(t.indexOf(": ") + 2, t.length());
            
            upcomingEvents.add(new SchoolEvent(eventDate, name));
            
         }
         
         portalInfo = new PortalInfo(courses, grades, upcomingEvents, mp);
         
      }
      return portalInfo;
   }
   
   public List<Course> getCourses(MarkingPeriod markingPeriod) {return null;}
   public Map<Course, CourseAssignments> getCourseAssignments(MarkingPeriod markingPeriod) {return null;}
   public List<FinalGrade> getFinalGrades() {return null;}
   public List<FinalExam> getFinalExams() {return null;}
   public List<GraduationRequirement> getGraduationRequrements() {return null;}
   public List<SchoolEvent> getEventsFromCalendar(int yearStart, int yearEnd) {return null;}
   public StudentInformation getStudentInformation(MarkingPeriod markingPeriod) {return null;}
   
}
