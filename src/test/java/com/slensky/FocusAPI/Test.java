package com.slensky.FocusAPI;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.slensky.FocusAPI.struct.Course;

public class Test {

   public static void main(String[] args) throws IOException {
      
      Focus focus = new Focus();
      
      @SuppressWarnings("resource")
      Scanner scan = new Scanner(System.in);
      
      System.out.print("Username: ");
      String user = scan.nextLine();
      System.out.print("Password: ");
      String pass = readPassword();
      
      focus.logIn(user, pass);
      
      ArrayList<Course> courses = focus.getClasses();
      for (Course c : courses) {
         System.out.println(c);
      }
      
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
