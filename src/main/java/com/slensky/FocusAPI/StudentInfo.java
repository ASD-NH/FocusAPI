package com.slensky.FocusAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.slensky.FocusAPI.studentinfo.Assignment;
import com.slensky.FocusAPI.studentinfo.Course;
import com.slensky.FocusAPI.studentinfo.CourseAssignments;
import com.slensky.FocusAPI.studentinfo.FinalExam;
import com.slensky.FocusAPI.studentinfo.FinalGrade;
import com.slensky.FocusAPI.studentinfo.Grade;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod.Term;
import com.slensky.FocusAPI.studentinfo.PortalInfo;
import com.slensky.FocusAPI.studentinfo.SchoolEvent;
import com.slensky.FocusAPI.studentinfo.StudentAccountInfo;
import com.slensky.FocusAPI.util.Logger;
import com.slensky.FocusAPI.util.URLRetriever;
import com.slensky.FocusAPI.util.Util;

public class StudentInfo {
   
   private final Focus focus;
   private final FocusDownloader downloader;
   
   private MarkingPeriod currentMarkingPeriod;
   
   private List<MarkingPeriod> markingPeriods = new ArrayList<MarkingPeriod>();
   
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
      Document portal = focus.getDownloader().getPortal().get(mp);
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
         List<DayOfWeek> meetingDays;
         
         String[] courseInfo = t.split(" - ");
         name = courseInfo[0];
         teacher = courseInfo[4];
         
         String periodStr = courseInfo[1].substring(courseInfo[1].length() - 1, courseInfo[1].length());
         period = Integer.parseInt(periodStr);
         
         meetingDays = Util.parseMeetingDays(courseInfo[2]);
         
         courses.add(new Course(period, name, teacher, meetingDays, null, new MarkingPeriod(mp.getYear(), null, mp.getMarkingPeriodId())));
         
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
         Calendar eventDate = Util.parseDate(date);
         
         String name = t.substring(t.indexOf(": ") + 2, t.length());
         
         upcomingEvents.add(new SchoolEvent(eventDate, name));
         
      }
      
      return new PortalInfo(courses, grades, upcomingEvents, mp);
      
   }
   
   public List<Course> getCoursesFromPortalInfo(MarkingPeriod mp) throws SessionExpiredException, IOException {
      return getPortalInfo(mp).getCourses();
   }
   
   public List<Course> getCoursesFromSchedule(MarkingPeriod mp) throws SessionExpiredException, IOException {
      if (downloader.getScheduleCSV() == null) {
         if (downloader.getSchedule() == null) {
            Document portal = focus.getDownloader().getPortal().get(mp);
            String scheduleURL = portal.getElementsContainingOwnText("› Class Schedule / Registration").get(0).attr("href");
            scheduleURL = URLRetriever.getTLD() + scheduleURL;
            downloader.setSchedule(new FocusDocument(downloader, scheduleURL));
            
            Logger.log("Retrieved schedule URL: " + scheduleURL);
         }
         String csvLink = getCSVLink(downloader.getSchedule().get(mp));
         downloader.setScheduleCSV(new FocusCSV(downloader, csvLink));
      }
      
      String csv = downloader.getScheduleCSV().get(mp);
      
      List<Course> courses = new ArrayList<Course>();
      String[] lines = csv.split("\n");
      for (String s : lines) {
         //skip the header field
         if (s.indexOf("\"Course\"") >= 0) {
            continue;
         }
         //skip the advisory field
         if (s.indexOf("Advisory") >= 0) {
            continue;
         }
         
         String[] fields = s.split(",");
         
         String name = fields[0];
         
         String[] data = fields[1].split(" - ");
         int period = Integer.parseInt(data[0].substring(7, data[0].length()));
         List<DayOfWeek> meetingDays = Util.parseMeetingDays(data[1]);
         String teacher = data[3];
         
         String roomNumber = fields[3];
         
         Term term = Util.parseTerm(fields[4]);
         int mpYear = mp.getYear();
         Term mpTerm = term;
         int markingPeriodId = mp.getMarkingPeriodId();
         if (term == Term.FULL_YEAR) {
            for (MarkingPeriod m : markingPeriods) {
               if (m.getYear() == mpYear) {
                  markingPeriodId = m.getMarkingPeriodId();
                  break;
               }
            }
         }
         
         courses.add(new Course(period, name, teacher, meetingDays, roomNumber, new MarkingPeriod(mpYear, mpTerm, markingPeriodId)));
         
      }
      
      return courses;
   }
   
   public CourseAssignments getCourseAssignments(Course course, MarkingPeriod mp) throws SessionExpiredException, IOException {
      
      Document portal = downloader.getPortal().get(mp);
      String courseLink = null;
      for (Element e : portal.getElementsByAttributeValueStarting("href", "Modules.php?modname=Grades/StudentGBGrades.php?course_period_id=")) {
         if (e.text().indexOf(course.getName()) >= 0) {
            courseLink = URLRetriever.getTLD() + e.attr("href");
            break;
         }
      }
      assert courseLink != null : "Link for the selected course could not be found!";
      Logger.log("Link to " + course.getName() + " extracted: " + courseLink);
      
      Document coursePage = downloader.getDocument(mp, courseLink);
      
      Map<String, Double> categories = null;
      
      //has categories probably
      if (coursePage.text().contains("Percent of Grade") &&
            coursePage.text().contains("Your Score") &&
            coursePage.text().contains("Weighted Grade")) {
         Element categoryTable = coursePage.getElementById("Page_Content").getElementsByTag("table").get(1)
               .getElementsByTag("tbody").get(1);
         
         Elements categoryNameElements = categoryTable.getElementsByTag("tr").get(0).getElementsByTag("td");
         Elements categoryWeightElements = categoryTable.getElementsByTag("tr").get(1).getElementsByTag("td");
         
         categories = new HashMap<String, Double>();
         
         List<String> categoryNames = new ArrayList<String>();
         for (Element e : categoryNameElements) {
            if (e.html().contains("&nbsp;")) {
               break;
            }
            categoryNames.add(e.text());
         }
         List<Double> categoryWeights = new ArrayList<Double>();
         for (Element e : categoryWeightElements) {
            if (e.html().contains("&nbsp;")) {
               break;
            }
            String numOnly = e.text().substring(0, e.text().length() - 1);
            categoryWeights.add(Double.parseDouble(numOnly) / 100);
         }
         
         assert categoryNames.size() == categoryWeights.size() : "Mismatch between number of weights and number of names";
         
         for (int i = 0; i < categoryNames.size(); i++) {
            categories.put(categoryNames.get(i), categoryWeights.get(i));
         }
         Logger.log(categories.toString());
      }
      else {
         Logger.log("No categories found for course");
      }
      
      List<Assignment> assignments = new ArrayList<Assignment>();
      Elements assignmentsDOM = coursePage.getElementsByAttributeValueStarting("id", "LOy_row");
      for (Element e : assignmentsDOM) {
         Elements fields = e.getElementsByClass("LO_field");
         
         String name = fields.get(0).text();
         
         String desc = "";
         if (fields.get(0).children().size() > 0) {
            desc = fields.get(0).child(0).attr("onmouseover");
            desc = desc.substring(desc.indexOf("\",\"") + 3, desc.indexOf("\"]"));
            desc = StringEscapeUtils.unescapeJava(desc);
         }
         
         Element gradeDOM = fields.get(1);
         Grade.Type gradeType;
         double[] gradeData = new double[2];
         //is a pass or fail grade
         if (gradeDOM.children().size() > 0) {
            String src = gradeDOM.child(0).attr("src");
            if (src.contains("check")) {
               gradeType = Grade.Type.PASS;
            }
            else {
               gradeType = Grade.Type.FAIL;
            }
         }
         else {
            String[] gradePair = gradeDOM.text().toLowerCase().split(" / ");
            assert gradePair.length == 2 : "Couldn't identify grade pair";
            //absent
            if (gradePair[0].contains("ab")) {
               gradeType = Grade.Type.ABSENT;
               gradeData[0] = Double.parseDouble(gradePair[1]);
            }
            //excused
            else if (gradePair[0].contains("*")) {
               gradeType = Grade.Type.EXCUSED;
               gradeData[0] = Double.parseDouble(gradePair[1]);
            }
            //not graded
            else if (gradePair[0].contains("ng")) {
               gradeType = Grade.Type.NOT_GRADED;
               gradeData[0] = Double.parseDouble(gradePair[1]);
            }
            //graded
            else {
               gradeType = Grade.Type.GRADED;
               gradeData = new double[2];
               gradeData[0] = Double.parseDouble(gradePair[0]);
               gradeData[1] = Double.parseDouble(gradePair[1]);
            }
         }
         
         Grade grade = new Grade(gradeType, gradeData);
         
         Calendar assigned = Util.parseDate(fields.get(4).text());
         Calendar due = Util.parseDate(fields.get(5).text());
         
         Calendar lastModified;
         String category = null;
         if (categories == null) {
            lastModified = Util.parseDate(fields.get(8).text());
         }
         else {
            category = fields.get(6).text();
            
            String lastModifiedStr = fields.get(9).text();
            if (lastModifiedStr.isEmpty()) {
               lastModified = null;
            }
            else {
               lastModified = Util.parseDate(lastModifiedStr);
            }
         }
         
         assignments.add(new Assignment(name, desc, grade, assigned, due, lastModified, category));
         
      }
      
      CourseAssignments courseAssignments = new CourseAssignments(assignments, categories);
      return courseAssignments;
      
   }
   
   public List<FinalGrade> getFinalGrades() {return null;}
   public List<FinalExam> getFinalExams() {return null;}
   public List<SchoolEvent> getEventsFromCalendar(int yearStart, int yearEnd) {return null;}
   public StudentAccountInfo getStudentInformation(MarkingPeriod mp) {return null;}
   
   private String getCSVLink(Document document) {
      Element csvLinkElement = document.getElementById("lo_controls").getElementsByTag("a").get(0);
      String csvLink = URLRetriever.getTLD() + csvLinkElement.attr("href");
      Logger.log("CSV extracted: " + csvLink);
      return csvLink;
   }
   
}
