package com.slensky.FocusAPI;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

import com.slensky.FocusAPI.studentinfo.MarkingPeriod;
import com.slensky.FocusAPI.util.Logger;

public class Test {

   public static void main(String[] args) throws SessionExpiredException {
      
      @SuppressWarnings("resource")
      Scanner scan = new Scanner(System.in);
      
      System.out.println("Focus API v0.1.0 alpha test-only version");
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
      
      
      long last = (focus.getDownloader().getSessExpiration() - System.currentTimeMillis()) / 1000;
      while (true) {
         long current = (focus.getDownloader().getSessExpiration() - System.currentTimeMillis()) / 1000;
         if (current != last) {
            Logger.log("Session expires in " + current + " seconds");
            last = current;
         }
      }
      
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
