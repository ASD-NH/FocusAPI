package com.slensky.FocusAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.slensky.FocusAPI.studentinfo.MarkingPeriod;

public class FocusCSV {
   
   private final Map<MarkingPeriod, String> cachedMap = new HashMap<MarkingPeriod, String>();
   private final String url;
   private final FocusDownloader downloader;
   
   public FocusCSV(FocusDownloader downloader, String url) {
      this.downloader = downloader;
      this.url = url;
   }
   
   public void download(MarkingPeriod mp, boolean force) throws IOException, SessionExpiredException {
      if (!cachedMap.containsKey(mp) || force) {
         String csv = downloader.getCSV(mp, url);
         cachedMap.put(mp, csv);
      }
   }
   
   public void cache(MarkingPeriod mp, String csv, boolean overwrite) {
      if (!cachedMap.containsKey(mp) || overwrite) {
         cachedMap.put(mp, csv);
      }
   }
   
   public String get(MarkingPeriod mp) throws IOException, SessionExpiredException {
      if (cachedMap.containsKey(mp)) {
         return cachedMap.get(mp);
      }
      else {
         String csv = downloader.getCSV(mp, url);
         cachedMap.put(mp, csv);
         return csv;
      }
   }
   
}
