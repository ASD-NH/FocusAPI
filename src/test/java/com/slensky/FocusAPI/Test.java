package com.slensky.FocusAPI;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.FailedLoginException;

public class Test {

   public static void main(String[] args) {
      
      @SuppressWarnings("resource")
      Scanner scan = new Scanner(System.in);
      
      System.out.println("Focus API v0.0.3 alpha test-only version");
      System.out.println("Do not redistribute without express permission\n");
      
      Focus focus = null;
      while (true) {
         System.out.print("Username: ");
         String user = scan.nextLine();
         System.out.print("Password: ");
         String pass = readPassword();
         try {
            System.out.println();
            focus = new Focus(user, pass);
            break;
         } catch(FailedLoginException e) {
            System.out.println("Login failed. Please try again");
         } catch(IOException e) {
            System.out.println("Connection timed out, please try again.");
         }
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
