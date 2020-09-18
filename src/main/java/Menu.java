import java.util.Scanner;

public class Menu {


    public void start(boolean loggedIn) {
        if (loggedIn)
            loggedInMenuActions();
        else
            beforeLoggedInMenuActions();
    }


    public void beforeLoggedInMenuActions() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    public void loggedInMenuActions() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }




}


