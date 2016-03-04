package com.slensky.FocusAPI.struct;

import java.util.ArrayList;

import com.slensky.FocusAPI.util.Constants;

public class Course {
   
   public final int period;
   public final String name;
   public final String teacher;
   public final ArrayList<Constants.day> meetingDays;
   public final int roomNumber;
   public final Constants.termLength termLength;
   
   public Course(int period,
                 String name,
                 String teacher,
                 ArrayList<Constants.day> meetingDays,
                 int roomNumber,
                 Constants.termLength termLength) {
      this.period = period;
      this.name = name;
      this.teacher = teacher;
      this.meetingDays = meetingDays;
      this.roomNumber = roomNumber;
      this.termLength = termLength;
   }
   
   public String toString() {
      return name + " - " + "Period " + period + " - " + teacher + " - " + roomNumber;
   }
   
}
