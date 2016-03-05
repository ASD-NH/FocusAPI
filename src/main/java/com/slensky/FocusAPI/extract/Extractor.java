package com.slensky.FocusAPI.extract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.slensky.FocusAPI.struct.Course;
import com.slensky.FocusAPI.struct.PageIndex;
import com.slensky.FocusAPI.util.Constants;

public class Extractor {
   
   private static ArrayList<Course> courses;
   
   public static ArrayList<Course> extractCourses(PageIndex index) throws IOException {
      
      if (courses == null) {
         
         courses = new ArrayList<Course>();
         ArrayList<String> matches = new ArrayList<String>();
         
         String courseInfoHTML = index.getSchedule().select("div#center").html();
         
         Matcher allDataFields = Pattern.compile("<td class=\"LO_field\">.+?</td>")
               .matcher(courseInfoHTML);
         while (allDataFields.find()) {
            matches.add(allDataFields.group());
         }
         for (int i = 0; i < matches.size(); i++) {
            matches.set(i, matches.get(i).substring(21, matches.get(i).length() - 5));
         }
         
         for (int i = 0; i < 9; i++) {
            
            int period;
            String name;
            String teacher;
            ArrayList<Constants.day> meetingDays = new ArrayList<Constants.day>();
            String roomNumber;
            Constants.termLength termLength = null;
            
            //get the name of the class
            name = matches.get(i * 5);
            //skip advisory
            if (name.indexOf("Advisory") >= 0) {
               continue;
            }
            
            String periodTeacher = matches.get((i * 5) + 1);
            
            //get the period
            period = Integer.parseInt(String.valueOf(periodTeacher.charAt(7)));
            
            //get the days the class meets
            String meetingDaysRaw = matches.get((i * 5) + 2);
            for (char d : meetingDaysRaw.toCharArray()) {
               switch(d) {
                  case 'M':
                     meetingDays.add(Constants.day.MONDAY);
                  case 'T':
                     meetingDays.add(Constants.day.TUESDAY);
                  case 'W':
                     meetingDays.add(Constants.day.WEDNESDAY);
                  case 'H':
                     meetingDays.add(Constants.day.THURSDAY);
                  case 'F':
                     meetingDays.add(Constants.day.FRIDAY);
               }         
            }
            
            //get the teacher of the class
            Matcher inverseTeacherMatcher = Pattern.compile("((Period [0-9]|[A-Z][0-9]) - ){1,2}[A-Z]{3} - [0-9]{3} - ")
                  .matcher(periodTeacher);
            inverseTeacherMatcher.find();
            String inverseTeacher = inverseTeacherMatcher.group();
            teacher = periodTeacher.substring(inverseTeacher.length());
            
            //get the room number of the class
            roomNumber = matches.get((i * 5) + 3);
            
            //get the duration of the class
            String duration = matches.get((i * 5) + 4);
            if (duration.equals("Full Year")) {
               termLength = Constants.termLength.FULL_YEAR;
            }
            else if (duration.equals("Semester 1")) {
               termLength = Constants.termLength.SEM_ONE;
            }
            else if (duration.equals("Semester 2")) {
               termLength = Constants.termLength.SEM_TWO;
            }
            
            courses.add(new Course(period, name, teacher, meetingDays, roomNumber, termLength));
            
         }
      
      }
      
      return courses;
   }
   
}
