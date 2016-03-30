package com.slensky.FocusAPI.studentinfo;

import java.util.List;

public class PortalInfo {
   
   private List<Course> courses;
   private List<Integer> grades;
   private List<SchoolEvent> upcomingEvents;
   private MarkingPeriod mp;
   
   public PortalInfo(List<Course> courses,
                     List<Integer> grades,
                     List<SchoolEvent> upcomingEvents,
                     MarkingPeriod mp) {
      assert(courses.size() == grades.size());
      this.courses = courses;
      this.grades = grades;
      this.upcomingEvents = upcomingEvents;
      this.mp = mp;
   }
   
   public List<Course> getCourses() {
      return courses;
   }
   public List<Integer> getGrades() {
      return grades;
   }
   public List<SchoolEvent> getUpcomingEvents() {
      return upcomingEvents;
   }
   public MarkingPeriod getMarkingPeriod() {
      return mp;
   }
   
}
