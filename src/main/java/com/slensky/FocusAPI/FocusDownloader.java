package com.slensky.FocusAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

import com.slensky.FocusAPI.cookie.Cookie;
import com.slensky.FocusAPI.cookie.CurrentSession;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.document.Portal;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.util.Constants;
import com.slensky.FocusAPI.util.Logger;
import com.slensky.FocusAPI.util.URLRetriever;

public class FocusDownloader {
   
   private final Focus focus;
   
   private long sessExpiration;
   
   private Portal portal;
   private String markingPeriodJSON;
   
   public FocusDownloader(Focus focus) {
      this.focus = focus;
      this.portal = new Portal(this);
   }
   
   public long getSessExpiration() {
      return sessExpiration;
   }
   public void setSessionExpiration(long sessionExpiration) {
      this.sessExpiration = sessionExpiration;
   }
   
   public void downloadPortal(MarkingPeriod mp, boolean force) throws IOException, SessionExpiredException {
      portal.downloadPortal(mp, force);
   }
   public void cachePortal(MarkingPeriod mp, Document portal, boolean overwrite) {
      this.portal.cachePortal(mp, portal, overwrite);
   }
   public Document getPortal(MarkingPeriod mp) throws IOException, SessionExpiredException {
      return portal.getPortal(mp);
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
      Document document = Jsoup.connect(URLRetriever.getTLD())
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
   
   private void ensureLogin() throws SessionExpiredException {
      //give 10 seconds of flex time
      if (System.currentTimeMillis() > (sessExpiration - (10 * 1000))) {
         throw new SessionExpiredException("Session has expired. Please call the logIn() function of the Focus object to handle this exception.");
      }
   }
   
}
