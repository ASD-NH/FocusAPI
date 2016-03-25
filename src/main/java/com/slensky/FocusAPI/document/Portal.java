package com.slensky.FocusAPI.document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.Focus.School;
import com.slensky.FocusAPI.FocusDownloader;
import com.slensky.FocusAPI.SessionExpiredException;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.util.URLRetriever;
import com.slensky.FocusAPI.util.Util;

public class Portal {
   
   private final Map<MarkingPeriod, Document> portalMap = new HashMap<MarkingPeriod, Document>();
   private final FocusDownloader downloader;
   
   public Portal(FocusDownloader downloader) {
      this.downloader = downloader;
   }
   
   public void downloadPortal(MarkingPeriod mp, boolean force) throws IOException, SessionExpiredException {
      if (!portalMap.containsKey(mp) || force) {
         Document portal = downloader.getDocument(mp, URLRetriever.getTLD());
         portalMap.put(mp, portal);
      }
   }
   
   public void cachePortal(MarkingPeriod mp, Document portal, boolean overwrite) {
      if (!portalMap.containsKey(mp) || overwrite) {
         portalMap.put(mp, portal);
      }
   }
   
   public Document getPortal(MarkingPeriod mp) throws IOException, SessionExpiredException {
      if (portalMap.containsKey(mp)) {
         return portalMap.get(mp);
      }
      else {
         Document portal = downloader.getDocument(mp, URLRetriever.getTLD());
         portalMap.put(mp, portal);
         return portal;
      }
   }
   
}
