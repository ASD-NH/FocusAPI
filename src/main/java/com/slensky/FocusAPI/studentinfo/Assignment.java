package com.slensky.FocusAPI.studentinfo;

import java.util.Calendar;

public class Assignment {

   private final String name;
   private final Grade grade;
   private final Calendar assigned;
   private final Calendar due;
   private final Calendar lastModified;
   private final String category;
   private final boolean hasCategory;
   private final String comment;
   private final boolean hasComment;
   
   public Assignment(String name,
         String comment,
         Grade grade,
         Calendar assigned,
         Calendar due,
         Calendar lastModified,
         String category) {

      this.name = name;
      this.grade = grade;
      this.assigned = assigned;
      this.due = due;
      this.lastModified = lastModified;
      
      this.comment = comment;
      if (comment == null) {
         this.hasComment = false;
      }
      else {
         this.hasComment = true;
      }
      
      this.category = category;
      if (category == null) {
         this.hasCategory = false;
      }
      else {
         this.hasCategory = true;
      }
      
   }
   
   public String toString() {
      String out = name + " - Assigned " + 
            (assigned.get(Calendar.MONTH) + 1) + "/" + assigned.get(Calendar.DAY_OF_MONTH) +
            " - Due " + (assigned.get(Calendar.MONTH) + 1) + "/" + due.get(Calendar.DAY_OF_MONTH);
      if (hasCategory) {
         out += " - " + category;
      }
      return out;
   }

   /* Accessors */
   public String getName() {
      return name;
   }
   public Grade getGrade() {
      return grade;
   }
   public Calendar getDateAssigned() {
      return assigned;
   }
   public Calendar getDateDue() {
      return due;
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
   public String getCategory() {
      return category;
   }
   public boolean hasCategory() {
      return hasCategory;
   }
   
}
