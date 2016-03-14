package com.slensky.FocusAPI.studentinfo;

public class MarkingPeriod {
   
   public static final MarkingPeriod MOST_RECENT = null;
   public static enum Term {
      QUARTER_ONE,
      QUARTER_TWO,
      QUARTER_THREE,
      QUARTER_FOUR,
      SEMESTER_ONE,
      SEMESTER_TWO,
      TRIMESTER_ONE,
      TRIMESTER_TWO,
      TRIMESTER_THREE
   };
   
   private final int year;
   private final Term term;
   private final int markingPeriodId;
   
   public MarkingPeriod(int year, Term term, int markingPeriodId) {
      this.year = year;
      this.term = term;
      this.markingPeriodId = markingPeriodId;
   }
   
   public int getYear() {
      return year;
   }
   public Term getTerm() {
      return term;
   }
   public int getMarkingPeriodId() {
      return markingPeriodId;
   }
   
}
