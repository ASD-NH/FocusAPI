package com.slensky.FocusAPI.studentinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseAssignments {
   
   private final List<Assignment> assignments;
   private final Map<String, Double> categoryWeights;
   private final Map<String, Boolean> isCategoryGraded;
   private final boolean hasCategories;
   private final boolean isGraded;
   
   public CourseAssignments(List<Assignment> assignments, Map<String, Double> categoryWeights) {
      
      this.assignments = assignments;
      
      //set up whether or not the course overall is graded
      boolean isGraded = false;
      for (Assignment a : assignments) {
         if (a.getGrade().isGraded()) {
            isGraded = true;
            break;
         }
      }
      this.isGraded = isGraded;
      
      //set up category weights
      this.categoryWeights = categoryWeights;
      if (categoryWeights != null) {
         hasCategories = true;
      }
      else {
         hasCategories = false;
      }
      
      //set up category isGraded map
      if (hasCategories) {
         isCategoryGraded = new HashMap<String, Boolean>();
         for (String c : getCategories()) {
            List<Assignment> categoryAssignments = getAssignmentsForCategory(c);
            boolean categoryGraded = false;
            for (Assignment a : categoryAssignments) {
               if (a.getGrade().isGraded()) {
                  categoryGraded = true;
                  break;
               }
            }
            this.isCategoryGraded.put(c, categoryGraded);
         }
      }
      else {
         isCategoryGraded = null;
      }
      
   }
   
   public Double getOverallGrade() {
      if (isGraded) {
         double overallGrade = 0;
         for (String s : getCategories()) {
            overallGrade += getCategoryGrade(s) * categoryWeights.get(s);
         }
         return overallGrade;
      }
      else {
         return null;
      }
   }
   
   public Double getCategoryGrade(String category) {
      List<Assignment> assignmentsForCategory = getAssignmentsForCategory(category);
      if (assignmentsForCategory != null && isCategoryGraded.get(category)) {
         double pointsReceived = 0;
         double pointsMax = 0;
         for (Assignment a : assignmentsForCategory) {
            if (a.getGrade().isGraded()) {
               pointsReceived += a.getGrade().getPointsReceived();
               pointsMax += a.getGrade().getPointsMax();
            }
         }
         return pointsReceived / pointsMax;
      }
      else {
         return null;
      }
   }
   
   
   public List<Assignment> getAssignments() {
      return assignments;
   }
   public List<Assignment> getAssignmentsForCategory(String category) {
      List<Assignment> assignmentsForCategory = new ArrayList<Assignment>();
      for (Assignment a : assignments) {
         if (a.getCategory().equals(category)) {
            assignmentsForCategory.add(a);
         }
      }
      if (assignmentsForCategory.isEmpty()) {
         return null;
      }
      else {
         return assignmentsForCategory;
      }
   }
   public Set<String> getCategories() {
      return categoryWeights.keySet();
   }
   public Set<String> getGradedCategories() {
      Set<String> gradedCategories = new HashSet<String>();
      for (String c : getCategories()) {
         if (isCategoryGraded.get(c)) {
            gradedCategories.add(c);
         }
      }
      if (gradedCategories.isEmpty()) {
         return null;
      }
      else {
         return gradedCategories;
      }
   }
   public boolean hasCategories() {
      return hasCategories;
   }
   public boolean isGraded() {
      return isGraded;
   }
   
   public String toString() {
      String out = "";
      out += isGraded ? "Overall grade: " + Double.toString(getOverallGrade() * 100) + "%" : "Not graded";
      out += hasCategories() ? " - Number of categories: " + getCategories().size() : " - No categories";
      if (hasCategories()) {
         for (String c : getCategories()) {
            out += " - " + c + ": " + Double.toString(getCategoryGrade(c) * 100) + "%";
         }
      }
      
      return out;
   }
   
}
