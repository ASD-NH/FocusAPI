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
   
}
