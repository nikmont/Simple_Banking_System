package banking;

import org.sqlite.SQLiteDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankingSystem {

    static Scanner scan;
    SQLiteDataSource dataSource;
    Connection con;
    static String DATABASE_URL;

    public BankingSystem(String database) {
        scan = new Scanner(System.in);
        dataSource = new SQLiteDataSource();
        DATABASE_URL = "jdbc:sqlite:" + database;
        connectDB(DATABASE_URL);
    }

    public void run() {
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
                    try {
                        con.close();
                    } catch (SQLException e) {
                        System.out.println("Error at closing connection");
                        e.printStackTrace();
                    }
                    System.out.println("\nBye!");
                    break;
            }
        }
    }

    private void connectDB(String url) {
        dataSource.setUrl(url);
        try {
            con = dataSource.getConnection();
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS card("+
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                System.out.println("Error at query execute");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error at connection");
            e.printStackTrace();
        }
    }

    private void register() {
        Account newUser = new Account(0, CardGenerator.create());

            try (Statement statement = con.createStatement()) {
                statement.executeUpdate(
                        "INSERT INTO card (number, pin)\n" +
                                "VALUES('" + newUser.getCard().getNumber() + "', '" + newUser.getCard().getPin() + "')");
            } catch (SQLException e) {
                System.out.println("Error at query execute");
                e.printStackTrace();
            }

        System.out.printf("\nYour card has been created%nYour card number:%n%s%nYour card PIN:%n%s%n", newUser.getCard().getNumber(), newUser.getCard().getPin());
    }

    private void login() {
        boolean isExists = false;
        int dbBalance = 0;

        System.out.println("Enter your card number:");
        String num = scan.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scan.nextLine();

            try (Statement statement = con.createStatement()) {
                try (ResultSet existsCards = statement.executeQuery("SELECT * FROM card")) {
                    while (existsCards.next()) {

                        dbBalance = existsCards.getInt("balance");
                        String dbNum = existsCards.getString("number");
                        String dbPin = existsCards.getString("pin");

                        if (num.equals(dbNum) && pin.equals(dbPin)) {
                            isExists = true;
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error at query execute");
                e.printStackTrace();
            }

        if (isExists) {

            System.out.println("You have successfully logged in!");

            boolean isLogout = false;

            while (!isLogout) {

                System.out.println("\n1. Balance\n2. Log out\n0. Exit\n");
                int loginAction = Integer.parseInt(scan.nextLine());

                switch (loginAction) {
                    case 1:
                        System.out.println(dbBalance);
                        break;
                    case 2:
                        System.out.println("\nYou have successfully logged out!");
                        isLogout = true;
                        break;
                    case 0:
                        try {
                            con.close();
                        } catch (SQLException e) {
                            System.out.println("Error at closing connection");
                            e.printStackTrace();
                        }
                        System.out.println("\nBye!");
                        System.exit(0);
                }
            }
        } else {
            System.out.println("Wrong card number or PIN!");
        }
    }
}
