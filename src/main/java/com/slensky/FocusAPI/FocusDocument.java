package com.slensky.FocusAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.studentinfo.MarkingPeriod;

public class FocusDocument {
   
   private final Map<MarkingPeriod, Document> cachedMap = new HashMap<MarkingPeriod, Document>();
   private final String url;
   private final FocusDownloader downloader;
   
   public FocusDocument(FocusDownloader downloader, String url) {
      this.downloader = downloader;
      this.url = url;
   }
   
   public void download(MarkingPeriod mp, boolean force) throws IOException, SessionExpiredException {
      if (!cachedMap.containsKey(mp) || force) {
         Document doc = downloader.getDocument(mp, url);
         cachedMap.put(mp, doc);
      }
   }
   
   public void cache(MarkingPeriod mp, Document doc, boolean overwrite) {
      if (!cachedMap.containsKey(mp) || overwrite) {
         cachedMap.put(mp, doc);
      }
   }
   
   public Document get(MarkingPeriod mp) throws IOException, SessionExpiredException {
      if (cachedMap.containsKey(mp)) {
         return cachedMap.get(mp);
      }
      else {
         Document doc = downloader.getDocument(mp, url);
         cachedMap.put(mp, doc);
         return doc;
      }
   }
   
}
