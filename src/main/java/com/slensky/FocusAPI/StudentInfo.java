package com.slensky.FocusAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
import com.slensky.FocusAPI.util.URLRetriever;
import com.slensky.FocusAPI.util.Util;

public class StudentInfo {
   
   private final Focus focus;
   private final FocusDownloader downloader;
   
   private List<MarkingPeriod> markingPeriods = new ArrayList<MarkingPeriod>();
   private MarkingPeriod currentMarkingPeriod;
   
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
   
   public List<Course> getCourses(MarkingPeriod markingPeriod) {return null;}
   public Map<Course, CourseAssignments> getCourseAssignments(MarkingPeriod markingPeriod) {return null;}
   public List<FinalGrade> getFinalGrades() {return null;}
   public List<FinalExam> getFinalExams() {return null;}
   public List<GraduationRequirement> getGraduationRequrements() {return null;}
   public List<SchoolEvent> getEventsFromCalendar(int yearStart, int yearEnd) {return null;}
   public StudentInformation getStudentInformation(MarkingPeriod markingPeriod) {return null;}
   
}
