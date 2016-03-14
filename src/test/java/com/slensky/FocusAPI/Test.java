package com.slensky.FocusAPI;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

public class Test {

   public static void main(String[] args) throws IOException {
      
      @SuppressWarnings("resource")
      Scanner scan = new Scanner(System.in);
      
      Focus focus = null;
      while (true) {
         System.out.print("Username: ");
         String user = "stephan.lensky";
         System.out.print("Password: ");
         String pass = "426-Summit";
         try {
            focus = new Focus(user, pass);
            break;
         } catch(FailedLoginException e) {
            System.out.println("Login failed. Please try again");
         }
      }
      
      /*long startTime;
      
      startTime = System.currentTimeMillis();
      ArrayList<Course> courses = focus.getClasses();
      System.out.println((System.currentTimeMillis() - startTime) + " ms elapsed to extract courses");
      for (Course c : courses) {
         //System.out.println(c);
      }
      
      Extractor.extractCourseAssignments(focus.pageIndex, focus.getClasses().get(0));
      */
   }
   
   private static String readPassword() {
      Console console = System.console();
      if (console == null) {
          System.out.println("Couldn't get Console instance");
          System.exit(0);
      }

      char passwordArray[] = console.readPassword("");
      
      return new String(passwordArray);
   }

}
