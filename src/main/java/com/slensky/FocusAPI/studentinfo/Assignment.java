package com.slensky.FocusAPI.studentinfo;

import java.util.Calendar;

public class Assignment {

   private final String name;
   private final Grade grade;
   private final Calendar dateAssigned;
   private final Calendar dateDue;
   private final Calendar lastModified;
   private final String comment;
   private final boolean hasComment;
   
   public Assignment(String name,
                     Grade grade,
                     Calendar dateAssigned,
                     Calendar dateDue,
                     Calendar lastModified) {
      
      this.name = name;
      this.grade = grade;
      this.dateAssigned = dateAssigned;
      this.dateDue = dateDue;
      this.lastModified = lastModified;
      this.comment = null;
      this.hasComment = false;
      
   }
   
   public Assignment(String name,
         String comment,
         Grade grade,
         Calendar dateAssigned,
         Calendar dateDue,
         Calendar lastModified) {

         this.name = name;
         this.grade = grade;
         this.dateAssigned = dateAssigned;
         this.dateDue = dateDue;
         this.lastModified = lastModified;
         this.comment = comment;
         this.hasComment = true;

   }

   /* Accessors */
   public String getName() {
      return name;
   }
   public Grade getGrade() {
      return grade;
   }
   public Calendar getDateAssigned() {
      return dateAssigned;
   }
   public Calendar getDateDue() {
      return dateDue;
   }
   public Calendar getLastModified() {
      return lastModified;
   }
   public String getComment() {
      return comment;
   }
   public boolean hasComment() {
      return hasComment;
   }
   
}
