package com.slensky.FocusAPI.studentinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseAssignments {
   
   private final List<AssignmentCategory> assignmentCategories;
   private final boolean hasCategories;
   private final boolean isGraded;
   
   public CourseAssignments(AssignmentCategory... assignmentCategories) {
      hasCategories = true;
      this.assignmentCategories = new ArrayList<AssignmentCategory>(Arrays.asList(assignmentCategories));
      
      boolean isGraded = false;
      for (AssignmentCategory c : assignmentCategories) {
         if (c.isGraded()) {
            isGraded = true;
         }
      }
      this.isGraded = isGraded;
   }
   
   public CourseAssignments(Assignment... assignments) {
      hasCategories = false;
      this.assignmentCategories = new ArrayList<AssignmentCategory>();
      this.assignmentCategories.add(new AssignmentCategory(null, 1.0, Arrays.asList(assignments)));
      
      boolean isGraded = false;
      for (Assignment a : assignments) {
         if (a.getGrade().isGraded()) {
            isGraded = true;
         }
      }
      this.isGraded = isGraded;
   }
   
   public Double getOverallGrade() {
      if (isGraded) {
         double overallGrade = 0;
         for (AssignmentCategory c : assignmentCategories) {
            if (c.isGraded()) {
               overallGrade += getCategoryGrade(c);
            }
         }
         return overallGrade;
      }
      else {
         return null;
      }
   }
   
   public static Double getCategoryGrade(AssignmentCategory category) {
      if (category.isGraded()) {
         double pointsRecieved = 0;
         double pointsMax = 0;
         for (Assignment a : category.getAssignments()) {
            Grade grade = a.getGrade();
            if (grade.isGraded() && !grade.isPassFail()) {
               pointsRecieved += grade.getPointsRecieved();
               pointsMax += grade.getPointsMax();
            }
         }
         return pointsRecieved / pointsMax;
      }
      return null;
   }
   
   /* Accessors */
   public List<AssignmentCategory> getAssignmentCategories() {
      return assignmentCategories;
   }
   public boolean hasCategories() {
      return hasCategories;
   }
   public boolean isGraded() {
      return isGraded;
   }
   
}
