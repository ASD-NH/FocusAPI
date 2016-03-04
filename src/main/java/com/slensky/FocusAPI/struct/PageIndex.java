package com.slensky.FocusAPI.struct;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.util.Constants;

public class PageIndex {
   
   private final String phpSessId;
   
   private Document portal;
   private Document classSchedule;
   
   public PageIndex(String phpSessId) {
      this.phpSessId = phpSessId;
   }
   
   public Document getPortal() throws IOException {
      if (portal == null) {
         portal = Jsoup.connect(Constants.FOCUS_PORTAL_URL.toString())
                 .cookie("PHPSESSID", phpSessId)
                 .get();
      }
      return portal;
   }
   
   public Document getSchedule() throws IOException {
      if (classSchedule == null) {
         classSchedule = Jsoup.connect(Constants.FOCUS_SCHEDULE_URL.toString())
                        .cookie("PHPSESSID", phpSessId)
                        .get();
      }
      return classSchedule;
   }
   
}
