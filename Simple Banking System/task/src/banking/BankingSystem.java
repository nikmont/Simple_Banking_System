package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankingSystem {

    static Scanner scan = new Scanner(System.in);
    static List<Account> accounts = new ArrayList<>();

    public static void run() {

        boolean isExit = false;

        while (!isExit) {

            System.out.println("\n1. Create an account\n2. Log into account\n0. Exit");

            int action = Integer.parseInt(scan.nextLine());

            switch (action) {
                case 1:
                    register();
                    break;
                case 2:
                   login();
                    break;
                case 0:
                    isExit = true;
                    System.out.println("\nBye!");
                    break;
            }
        }
    }

    public static void register() {
        Account newUser = new Account(0, CardGenerator.create());
        accounts.add(newUser);
        System.out.printf("\nYour card has been created%nYour card number:%n%s%nYour card PIN:%n%s%n", newUser.getCard().getNumber(), newUser.getCard().getPin());
    }

    public static void login() {
        System.out.println("Enter your card number:");
        String num = scan.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scan.nextLine();

        Account current = null;
        boolean isExists = false;
        for (Account account : accounts) {
            if (account.getCard().getNumber().equals(num) && account.getCard().getPin().equals(pin)) {
                current = account;
                isExists = true;
                break;
            }
        }

        if (isExists) {
            System.out.println("You have successfully logged in!");
            boolean isLogout = false;

            while (!isLogout) {

                System.out.println("\n1. Balance\n2. Log out\n0. Exit\n");
                int loginAction = Integer.parseInt(scan.nextLine());

                switch (loginAction) {
                    case 1:
                        System.out.println(current.getBalance());
                        break;
                    case 2:
                        System.out.println("\nYou have successfully logged out!");
                        isLogout = true;
                        break;
                    case 0:
                        System.out.println("\nBye!");
                        return;
                }
            }
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }
}
