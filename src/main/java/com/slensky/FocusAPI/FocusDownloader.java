package com.slensky.FocusAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.slensky.FocusAPI.cookie.Cookie;
import com.slensky.FocusAPI.cookie.CurrentSession;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.util.Constants;
import com.slensky.FocusAPI.util.Logger;
import com.slensky.FocusAPI.util.URLRetriever;

public class FocusDownloader {
   
   private final Focus focus;
   private long sessExpiration;
   
   private FocusDocument portal;
   private FocusDocument schedule;
   
   private FocusCSV scheduleCSV;
   
   private String markingPeriodJSON;
   
   public FocusDownloader(Focus focus) {
      this.focus = focus;
      this.portal = new FocusDocument(this, URLRetriever.getPortalURL());
   }
   
   public long getSessExpiration() {
      return sessExpiration;
   }
   public void setSessionExpiration(long sessionExpiration) {
      this.sessExpiration = sessionExpiration;
   }
   
   public FocusDocument getPortal() {
      return portal;
   }
   public void setSchedule(FocusDocument schedule) {
      this.schedule = schedule;
   }
   public FocusDocument getSchedule() {
      return schedule;
   }
   public void setScheduleCSV(FocusCSV scheduleCSV) {
      this.scheduleCSV = scheduleCSV;
   }
   public FocusCSV getScheduleCSV() {
      return scheduleCSV;
   }
   
   public void downloadMarkingPeriodJSON(boolean force) throws IOException {
      if (markingPeriodJSON == null || force) {
         URL jsonURL = null;
         try {
            jsonURL = new URL(URLRetriever.getMarkingPeriodURL());
         } catch (MalformedURLException e) {
            // should never happen
            e.printStackTrace();
         }
         markingPeriodJSON = IOUtils.toString(jsonURL);
      }
   }
   public void cacheMarkingPeriodJSON(String markingPeriodJSON, boolean overwrite) {
      if (this.markingPeriodJSON == null || overwrite) {
         this.markingPeriodJSON = markingPeriodJSON;
      }
   }
   public String getMarkingPeriodJSON() throws IOException {
      downloadMarkingPeriodJSON(false);
      return markingPeriodJSON;
   }
   
   public Document getDocument(MarkingPeriod mp, String url) throws IOException, SessionExpiredException {
      ensureLogin();
      setMarkingPeriod(mp);
      Map<String, String> cookieMap = new HashMap<String, String>();
      cookieMap.put(focus.getSessId().getName(), focus.getSessId().getContent());
      Document document = Jsoup.connect(url)
            .cookies(cookieMap)
            .timeout(Constants.CONNECTION_TIMEOUT)
            .get();
      
      return document;
   }
   
   public void setMarkingPeriod(MarkingPeriod markingPeriod) throws IOException, SessionExpiredException {
      ensureLogin();
      if (focus.getStudentInfo().getCurrentMarkingPeriod() != markingPeriod) {
         Logger.log("Changing marking period to " + markingPeriod.getTerm() + " " + markingPeriod.getYear() + " " + markingPeriod.getMarkingPeriodId());
         
         focus.getCurrSess().setYear(Integer.toString(markingPeriod.getYear()));
         focus.getCurrSess().setMarkingPeriod(Integer.toString(markingPeriod.getMarkingPeriodId()));
         
         Collection<Connection.KeyVal> formData = new HashSet<org.jsoup.Connection.KeyVal>();
         formData.add(HttpConnection.KeyVal.create("side_syear", Integer.toString(markingPeriod.getYear())));
         formData.add(HttpConnection.KeyVal.create("side_mp", Integer.toString(markingPeriod.getMarkingPeriodId())));
         
         //submit request to change years
         Jsoup.connect(URLRetriever.getPortalURL())
               .cookie(focus.getSessId().getName(), focus.getSessId().getContent())
               .data(formData)
               .method(Method.POST)
               .timeout(Constants.CONNECTION_TIMEOUT)
               .execute();
         
         focus.getStudentInfo().setCurrentMarkingPeriod(markingPeriod);
      }  
   }
   
   public String getCSV(MarkingPeriod mp, String url) throws IOException, SessionExpiredException {
      ensureLogin();
      setMarkingPeriod(mp);
      URL csvURL = null;;
      try {
         csvURL = new URL(url);
      } catch (MalformedURLException e) {
         //should hopefully never happen
         e.printStackTrace();
      }
      
      URLConnection conn = csvURL.openConnection();
      String cookie = focus.getSessId().getName() + "=" + focus.getSessId().getContent();
      conn.setRequestProperty("Cookie", cookie);
      conn.connect();
      
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder response = new StringBuilder();
      String inputLine;
      
      while ((inputLine = in.readLine()) != null) {
         response.append(inputLine + "\n");
      }
      
      in.close();
      
      return response.toString().substring(0, response.length() - 1);
   }
   
   private void ensureLogin() throws SessionExpiredException {
      //give 10 seconds of flex time
      if (System.currentTimeMillis() > (sessExpiration - (10 * 1000))) {
         throw new SessionExpiredException("Session has expired. Please call the logIn() function of the Focus object to handle this exception.");
      }
   }
   
}
