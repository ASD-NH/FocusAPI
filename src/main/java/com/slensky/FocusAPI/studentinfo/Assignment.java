package com.slensky.FocusAPI.studentinfo;

import java.util.Calendar;

public class Assignment {

   private final String name;
   private final Grade grade;
   private final Calendar dateAssigned;
   private final Calendar dateDue;
   private final Calendar lastModified;
   private final String category;
   private final boolean hasCategory;
   private final String comment;
   private final boolean hasComment;
   
   public Assignment(String name,
         String comment,
         Grade grade,
         Calendar dateAssigned,
         Calendar dateDue,
         Calendar lastModified,
         String category) {

      this.name = name;
      this.grade = grade;
      this.dateAssigned = dateAssigned;
      this.dateDue = dateDue;
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
            dateAssigned.get(Calendar.DAY_OF_MONTH) + " " + dateAssigned.get(Calendar.MONTH) +
            " - Due " + dateDue.get(Calendar.DAY_OF_MONTH) + " " + dateAssigned.get(Calendar.MONTH);
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
   public String getCategory() {
      return category;
   }
   public boolean hasCategory() {
      return hasCategory;
   }
   
}
