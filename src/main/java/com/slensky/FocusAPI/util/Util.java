package com.slensky.FocusAPI.util;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
   
   public static List<DayOfWeek> parseMeetingDays(String meetingDayString) {
      List<DayOfWeek> meetingDays = new ArrayList<DayOfWeek>();
      for (char d : meetingDayString.toLowerCase().toCharArray()) {
         switch (d) {
            case 'm':
               meetingDays.add(DayOfWeek.MONDAY);
               break;
            case 't':
               meetingDays.add(DayOfWeek.TUESDAY);
               break;
            case 'w':
               meetingDays.add(DayOfWeek.WEDNESDAY);
               break;
            case 'h':
               meetingDays.add(DayOfWeek.THURSDAY);
               break;
            case 'f':
               meetingDays.add(DayOfWeek.FRIDAY);
               break;
         }
      }
      return meetingDays;
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
      //is full year
      else {
         term = Term.FULL_YEAR;
      }
      
      return term;
   }
   
   public static Calendar parseDate(String date) {
      
      int month, day, year;
      int hour = -1, minute = -1;
      
      date = date.toLowerCase();
      String[] dayPrefixes = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
      String[] monthPrefixes = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
      
      /* get rid of the day label at the front if there is one */
      
      String potentialDay = date.substring(0, 3);
      boolean isDay = false;
      for (String d : dayPrefixes) {
         if (d.equals(potentialDay)) {
            isDay = true;
            break;
         }
      }
      
      if (isDay) {
         date = date.substring(4);
      }
      
      /* get rid of a leading zero for the month if there is one */
      
      if (date.charAt(0) == '0') {
         date = date.substring(1);
      }
      
      /* convert word month to numeric month */
      
      int numericFirstChar = Character.getNumericValue(date.charAt(0));
      if (!(numericFirstChar > 0 && numericFirstChar < 10)) {
         
         String prefix = date.substring(0, 3);
         
         int monthValue = 0;
         for (int i = 0; i < monthPrefixes.length; i++) {
            if (monthPrefixes[i].equals(prefix)) {
               monthValue = i + 1;
               break;
            }
         }
         assert monthValue != 0 : "Month could not be identified";
         
         month = monthValue;
         
      }
      else {
         month = Integer.parseInt(date.substring(0, date.indexOf(' ')));
      }
      
      date = date.substring(date.indexOf(' ') + 1);
      
      /* get day */
      
      String dayStr = date.substring(0, 2);
      if (dayStr.startsWith("0")) {dayStr = dayStr.substring(1);}
      if (dayStr.endsWith("t") || dayStr.endsWith(",")) {dayStr = dayStr.substring(0, 1);}
      day = Integer.parseInt(dayStr);
      
      /* get year */
      
      date = date.substring(date.indexOf(' ') + 1);
      date = date.replace(",", "");
      if (date.contains(" ")) {
         year = Integer.parseInt(date.substring(0, date.indexOf(' ')));
      }
      else {
         year = Integer.parseInt(date);
      }
      
      /* get time if there is one */
      
      if (date.contains(" ")) {
         date = date.substring(date.indexOf(' ') + 1);
         
         String[] hoursMinutes = date.substring(0, date.indexOf(' ')).split(":");
         assert hoursMinutes.length == 2 : "Error splitting time field";
         
         hour = Integer.parseInt(hoursMinutes[0]);
         minute = Integer.parseInt(hoursMinutes[1]);
         
         if (date.contains("pm")) {
            hour += 12;
         }
         if (hour == 12 || hour == 24) {
            hour -= 12;
         }
         
      }
      
      Calendar parsed = Calendar.getInstance();
      parsed.set(year, month - 1, day);
      if (hour != -1 && minute != -1) {
         parsed.set(Calendar.HOUR, hour);
         parsed.set(Calendar.MINUTE, minute);
      }
      
      return parsed;
   }
   
}
