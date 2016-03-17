package com.slensky.FocusAPI.document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.slensky.FocusAPI.Focus.School;
import com.slensky.FocusAPI.cookie.PHPSessionId;
import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.util.URLRetriever;
import com.slensky.FocusAPI.util.Util;

public class Portal {
   
   private final Map<MarkingPeriod, Document> portalMap = new HashMap<MarkingPeriod, Document>();
   
   public Document getPortal(MarkingPeriod markingPeriod, PHPSessionId sessId) throws IOException {
      if (portalMap.containsKey(markingPeriod)) {
         return portalMap.get(markingPeriod);
      }
      else {
         Document portal = Util.getDocument(URLRetriever.getTLD(), sessId);
         portalMap.put(markingPeriod, portal);
         return portal;
      }
   }
   
}
