package com.slensky.FocusAPI.studentinfo;

import java.util.List;

public class AssignmentCategory {

   private final String name;
   private final double weight;
   private final boolean isGraded;
   private final List<Assignment> assignments;
   
   public AssignmentCategory(String name,
                             double weight,
                             List<Assignment> assignments) {
      this.name = name;
      this.weight = weight;
      this.assignments = assignments;
      
      boolean isGraded = false;
      for (Assignment a : assignments) {
         if (a.getGrade().isGraded()) {
            isGraded = true;
            break;
         }
      }
      this.isGraded = isGraded;
   }

   /* Accessors */
   public String getName() {
      return name;
   }
   public double getWeight() {
      return weight;
   }
   public boolean isGraded() {
      return isGraded;
   }
   public List<Assignment> getAssignments() {
      return assignments;
   }
   
}
