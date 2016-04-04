package com.slensky.FocusAPI.studentinfo;

import java.time.DayOfWeek;
import java.util.List;

public class Course {
   
   private final int period;
   private final String name;
   private final String teacher;
   
   private final List<DayOfWeek> meetingDays;
   private final String roomNumber;
   private final MarkingPeriod markingPeriod;
   private final boolean isStudy;
   
   public Course(int period,
                 String name,
                 String teacher,
                 List<DayOfWeek> meetingDays,
                 String roomNumber,
                 MarkingPeriod markingPeriod) {
      
      this.period = period;
      this.name = name;
      this.teacher = teacher;
      this.meetingDays = meetingDays;
      this.roomNumber = roomNumber;
      this.markingPeriod = markingPeriod;
      
      boolean isStudy = false;
      if (name.toLowerCase().indexOf("study") >= 0) {
         isStudy = true;
      }
      this.isStudy = isStudy;
      
   }
   
   public String toString() {
      return name + " - " + "Period " + period + " - " + teacher + " - " + roomNumber;
   }

   /* Accessors */
   public int getPeriod() {
      return period;
   }
   public String getName() {
      return name;
   }
   public String getTeacher() {
      return teacher;
   }
   public List<DayOfWeek> getMeetingDays() {
      return meetingDays;
   }
   public String getRoomNumber() {
      return roomNumber;
   }
   public MarkingPeriod getMarkingPeriod() {
      return markingPeriod;
   }
   public boolean isStudy() {
      return isStudy;
   }
   
}
