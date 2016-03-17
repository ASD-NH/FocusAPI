package com.slensky.FocusAPI.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.Focus.School;
import com.slensky.FocusAPI.cookie.Cookie;
import com.slensky.FocusAPI.cookie.CurrentSession;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
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
   
   public static Document getDocument(String url, Cookie... cookies) throws IOException {
      
      Map<String, String> cookieMap = new HashMap<String, String>();
      for (Cookie c : cookies) {
         cookieMap.put(c.getName(), c.getContent());
      }
      
      return Jsoup.connect(URLRetriever.getTLD())
         .cookies(cookieMap)
         .timeout(Constants.CONNECTION_TIMEOUT)
         .get();
      
   }
   public static void setMarkingPeriod(MarkingPeriod markingPeriod, PHPSessionId sessId) throws IOException {
      Collection<Connection.KeyVal> formData = new HashSet<org.jsoup.Connection.KeyVal>();
      formData.add(HttpConnection.KeyVal.create("side_syear", Integer.toString(markingPeriod.getYear())));
      formData.add(HttpConnection.KeyVal.create("side_mp", Integer.toString(markingPeriod.getMarkingPeriodId())));
      
      //submit request to change years
      Jsoup.connect(URLRetriever.getTLD())
            .cookie(sessId.getName(), sessId.getContent())
            .data(formData)
            .method(Method.POST)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .execute();
   }
   
}
