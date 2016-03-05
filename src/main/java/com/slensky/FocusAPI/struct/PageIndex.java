package com.slensky.FocusAPI.struct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.extract.Extractor;
import com.slensky.FocusAPI.util.Constants;

public class PageIndex {
   
   private final String phpSessId;
   
   private Document portal;
   private Document classSchedule;
   private boolean coursesCached = false;
   private Map<Course, Document> coursePages = new HashMap<Course, Document>();
   
   public PageIndex(String phpSessId) {
      this.phpSessId = phpSessId;
   }
   
   public void cacheAll() {
      
   }
   
   public Document getPortal() throws IOException {
      if (portal == null) {
         portal = Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_PORTAL_URL)
                 .cookie("PHPSESSID", phpSessId)
                 .timeout(Constants.CONNECTION_TIMEOUT)
                 .get();
      }
      return portal;
   }
   
   public Document getSchedule() throws IOException {
      if (classSchedule == null) {
         classSchedule = Jsoup.connect(Constants.FOCUS_TLD + Constants.FOCUS_SCHEDULE_URL)
                        .cookie("PHPSESSID", phpSessId)
                        .timeout(Constants.CONNECTION_TIMEOUT)
                        .get();
      }
      return classSchedule;
   }
   
   private void cacheCoursePages() throws IOException {
      coursesCached = true;
      
      String portal = getPortal().html();
      ArrayList<String> courseNames = new ArrayList<String>();
      ArrayList<String> coursePageUrls = new ArrayList<String>();
      ArrayList<Course> allCourses = Extractor.extractCourses(this);
      
      Matcher courseLinkMatcher = Pattern.compile("Modules\\.php\\?modname=Grades\\/StudentGBGrades\\.php\\?course_period_id=\\d+\"><b>[A-Za-z0-9 ()]+")
            .matcher(portal);
      
      while (courseLinkMatcher.find()) {
         String match = courseLinkMatcher.group();
         
         String url = Constants.FOCUS_TLD + match.substring(0, match.indexOf('\"'));
         if (!coursePageUrls.contains(url)) {
            coursePageUrls.add(url);
         }
         
         String courseName = match.substring(match.indexOf("<b>") + 3, match.length() - 1);
         if (!courseNames.contains(courseName)) {
            courseNames.add(courseName);
         }
      }
      
      for (int i = 0; i < courseNames.size(); i++) {
         
         Course foundCourse = null;
         for (Course c : allCourses) {
            if (c.name.equals(courseNames.get(i))) {
               foundCourse = c;
            }
         }
         
         Document coursePage = Jsoup.connect(coursePageUrls.get(i))
                  .cookie("PHPSESSID", phpSessId)
                  .timeout(Constants.CONNECTION_TIMEOUT)
                  .get();
         coursePages.put(foundCourse, coursePage);
      }
   }
   
   public Document getCoursePage(Course course) throws IOException {
      
      if (course.isStudy()) {
         return null;
      }
      
      if (coursePages.containsKey(course)) {
         return coursePages.get(course);
      }
      
      String portal = getPortal().html();
      ArrayList<String> courseNames = new ArrayList<String>();
      ArrayList<String> coursePageUrls = new ArrayList<String>();
      
      //System.out.println(portal);
      
      Matcher courseLinkMatcher = Pattern.compile("Modules\\.php\\?modname=Grades\\/StudentGBGrades\\.php\\?course_period_id=\\d+\"><b>[A-Za-z0-9 ()]+")
            .matcher(portal);
      
      while (courseLinkMatcher.find()) {
         String match = courseLinkMatcher.group();
         
         String url = Constants.FOCUS_TLD + match.substring(0, match.indexOf('\"'));
         if (!coursePageUrls.contains(url)) {
            coursePageUrls.add(url);
         }
         
         String courseName = match.substring(match.indexOf("<b>") + 3, match.length() - 1);
         if (!courseNames.contains(courseName)) {
            courseNames.add(courseName);
         }
      }
      
      for (int i = 0; i < courseNames.size(); i++) {
         if (course.name.equals(courseNames.get(i))) {
            Document coursePage = Jsoup.connect(coursePageUrls.get(i))
                                 .cookie("PHPSESSID", phpSessId)
                                 .timeout(Constants.CONNECTION_TIMEOUT)
                                 .get();
            coursePages.put(course, coursePage);
            return coursePage;
         }
      }
      
      return null;
   }
   
   public Map<Course, Document> getAllCoursePages() throws IOException {
      if (!coursesCached) {
         cacheCoursePages();
      }
      return coursePages;
   }
   
}
