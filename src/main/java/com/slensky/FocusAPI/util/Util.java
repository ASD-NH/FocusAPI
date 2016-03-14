package com.slensky.FocusAPI.util;

import java.util.concurrent.ThreadLocalRandom;

import com.slensky.FocusAPI.studentinfo.MarkingPeriod.Term;

public class Util {

   public static String randomAlphanumeric(int length) {
      String output = "";
      
      for (int i = 0; i < length; i++) {
         int nextCharAscii = 0;
            
         while (!((nextCharAscii >= 48 && nextCharAscii <= 57) || (nextCharAscii >= 97 && nextCharAscii <= 122))) {
            nextCharAscii = ThreadLocalRandom.current().nextInt(48, 123);
         }
         
         output = output + (char) nextCharAscii;
      }
      
      return output;
   }
   
   public static Term parseTerm(String termStr) {
      Term term = null;
      termStr = termStr.toLowerCase();
      
      // is semester
      if (termStr.contains("sem")) {
         if (termStr.contains("1") || termStr.contains("one")) {
            term = Term.SEMESTER_ONE;
         }
         else if (termStr.contains("2") || termStr.contains("two")) {
            term = Term.SEMESTER_TWO;
         }
      }
      // is quarter
      else if (termStr.contains("quart")) {
         if (termStr.contains("1") || termStr.contains("one")) {
            term = Term.QUARTER_ONE;
         }
         else if (termStr.contains("2") || termStr.contains("two")) {
            term = Term.QUARTER_TWO;
         }
         else if (termStr.contains("3") || termStr.contains("three")) {
            term = Term.QUARTER_THREE;
         }
         else if (termStr.contains("4") || termStr.contains("four")) {
            term = Term.QUARTER_FOUR;
         }
      }
      //is trimester
      else if (termStr.contains("tri")) {
         if (termStr.contains("1") || termStr.contains("one")) {
            term = Term.TRIMESTER_ONE;
         }
         else if (termStr.contains("2") || termStr.contains("two")) {
            term = Term.TRIMESTER_TWO;
         }
         else if (termStr.contains("3") || termStr.contains("three")) {
            term = Term.TRIMESTER_THREE;
         }
      }
      
      return term;
   }
   
}
