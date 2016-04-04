package com.slensky.FocusAPI.studentinfo;

import java.util.Calendar;

public class SchoolEvent {
   
   private final Calendar eventDate;
   private final String name;
   private final String description;
   private final boolean isEventSchoolClosed;
   
   public SchoolEvent(Calendar eventDate, String name) {
      this.eventDate = eventDate;
      this.name = name;
      this.description = null;
      this.isEventSchoolClosed = isSchoolClosed();
   }
   public SchoolEvent(Calendar eventDate, String name, String description) {
      this.eventDate = eventDate;
      this.name = name;
      this.description = description;
      this.isEventSchoolClosed = isSchoolClosed();
   }
   
   private boolean isSchoolClosed() {
      String loweredName = name.toLowerCase();
      if (loweredName.indexOf("school closed") >= 0 ||
            loweredName.indexOf("school is closed") >= 0 ||
            loweredName.indexOf("no school") >= 0 ||
            loweredName.indexOf("teacher workday") >= 0 ||
            loweredName.indexOf("teacher workshop") >= 0) {
         return true;
      }
      else {
         return false;
      }
   }
   
   public String toString() {
      String out = "";
      out += (eventDate.get(Calendar.MONTH) + 1) + "/" + eventDate.get(Calendar.DAY_OF_MONTH) + "/" + eventDate.get(Calendar.YEAR) + ": ";
      out += name;
      if (description != null) {
         out += " - " + description;
      }
      return out;
   }
   
   public Calendar getEventDate() {
      return eventDate;
   }
   public String getName() {
      return name;
   }
   public String getDescription() {
      return description;
   }
   public boolean isEventSchoolClosed() {
      return isEventSchoolClosed;
   }
   
}
