package com.slensky.FocusAPI;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

import com.slensky.FocusAPI.studentinfo.Course;
import com.slensky.FocusAPI.util.Util;

public class Test {

   public static void main(String[] args) throws SessionExpiredException {
      
      @SuppressWarnings("resource")
      Scanner scan = new Scanner(System.in);
      
      System.out.println("Focus API v0.4.0 alpha test-only version");
      System.out.println("Do not redistribute without express permission\n");
      
      Focus focus = null;
      while (true) {
         System.out.print("Username: ");
         String user = scan.nextLine();
         System.out.print("Password: ");
         String pass = readPassword();
         try {
            System.out.println();
            focus = new Focus(user, pass, Focus.School.ASD);
            break;
         } catch(FailedLoginException e) {
            System.out.println("Login failed. Please try again");
         } catch(IOException e) {
            System.out.println("Connection timed out, please try again.");
         }
      }
      
      /*System.out.println("Portal Info:\n\n");
      try {
         System.out.println(focus.getStudentInfo().getPortalInfo(focus.getStudentInfo().getCurrentMarkingPeriod()));
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      System.out.println("\n\nSchedule Rip:\n");
      try {
         List<Course> courses = focus.getStudentInfo().getCoursesFromSchedule(focus.getStudentInfo().getCurrentMarkingPeriod());
         for (Course c : courses) {
            System.out.println(c);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }*/
      
   }
   
   private static String readPassword() {
      Console console = System.console();
      if (console == null) {
          System.out.println("Couldn't get Console instance. Make sure you're not using Eclipse to run this!");
          System.exit(0);
      }

      char passwordArray[] = console.readPassword("");
      
      return new String(passwordArray);
   }

}
