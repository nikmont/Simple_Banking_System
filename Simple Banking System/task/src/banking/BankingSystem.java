package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class BankingSystem {

    static Scanner scan;
    private final SQLiteDataSource dataSource;
    private Connection con;
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
        String insertNew = "INSERT INTO card (number, pin) VALUES(?, ?)";
        Account newUser = new Account(0, CardGenerator.create());

            try (PreparedStatement getCard = con.prepareStatement(insertNew)) {

                getCard.setString(1, newUser.getCard().getNumber());
                getCard.setString(2, newUser.getCard().getPin());

                getCard.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Error at adding new user");
                e.printStackTrace();
            }

        System.out.printf("\nYour card has been created%nYour card number:%n%s%nYour card PIN:%n%s%n", newUser.getCard().getNumber(), newUser.getCard().getPin());
    }

    private void login() {
        boolean isExists = false;
        String dbNum = "";
        String dbPin = "";

        System.out.println("Enter your card number:");
        String num = scan.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scan.nextLine();

            try (Statement statement = con.createStatement()) {
                try (ResultSet existsCards = statement.executeQuery("SELECT * FROM card")) {
                    while (existsCards.next()) {

                         dbNum = existsCards.getString("number");
                         dbPin = existsCards.getString("pin");

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

                System.out.println("\n1. Balance\n" +
                        "2. Add income\n" +
                        "3. Do transfer\n" +
                        "4. Close account\n" +
                        "5. Log out\n" +
                        "0. Exit");
                int loginAction = Integer.parseInt(scan.nextLine());

                switch (loginAction) {
                    case 1:
                        System.out.println("\n" + getBalance(dbNum, dbPin));
                        break;
                    case 2:
                        doDeposit(dbNum, dbPin);
                        break;
                    case 3:
                        doTransfer(dbNum, dbPin);
                        break;
                    case 4:
                        removeUser(dbNum, dbPin);
                        break;
                    case 5:
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

    private void doTransfer(String number, String pin) {
        String checkRecipientCard = "SELECT * FROM card WHERE number = ?";
        String minusBalance = "UPDATE card SET balance = balance - ? WHERE number = ? AND pin = ?";
        String plusBalance = "UPDATE card SET balance = balance + ? WHERE number = ?";

        System.out.println("Transfer\n" +
                "Enter card number:");
        String recipientCard = scan.nextLine();

        if (CardGenerator.luhnAlgh(recipientCard.substring(0, number.length() - 1)) !=
                Integer.parseInt(recipientCard.substring(number.length() - 1))) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        } else if (number.equals(recipientCard)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            try (PreparedStatement getCard = con.prepareStatement(checkRecipientCard);
                 PreparedStatement minus = con.prepareStatement(minusBalance);
                 PreparedStatement plus = con.prepareStatement(plusBalance)) {

                getCard.setString(1, recipientCard);
                ResultSet resultSet = getCard.executeQuery();

                if (resultSet.next()) {

                    System.out.println("Enter how much money you want to transfer:");
                    int moneyCount = Integer.parseInt(scan.nextLine());
                    if (getBalance(number, pin) - moneyCount < 0) {
                        System.out.println("Not enough money!");
                    } else {

                        con.setAutoCommit(false);

                        minus.setInt(1, moneyCount);
                        minus.setString(2, number);
                        minus.setString(3, pin);
                        minus.executeUpdate();

                        plus.setInt(1, moneyCount);
                        plus.setString(2, recipientCard);
                        plus.executeUpdate();

                        con.commit();
                        System.out.println("Success!");
                    }
                } else {
                    System.out.println("Such a card does not exist.");
                }
            } catch (SQLException ex) {
                System.out.println("Check card error");
                //rollback?
            }
        }
    }

    private void doDeposit(String number, String pin) {
        String updateBalance = "UPDATE card SET balance = balance + ? WHERE number = ? AND pin = ?";
        System.out.println("\nEnter income:");
        int income = Integer.parseInt(scan.nextLine());

        try (PreparedStatement updateBal = con.prepareStatement(updateBalance)) {

            updateBal.setInt(1, income);
            updateBal.setString(2, number);
            updateBal.setString(3, pin);

            updateBal.executeUpdate();

        } catch (SQLException ex) {
        System.out.println("Deposit error");
        //rollback?
    }
        System.out.println("Income was added!");
    }

    private int getBalance(String number, String pin) {
        String balanceQuery = "SELECT balance FROM card WHERE number = ? AND pin = ?";
        int balance = 0;

        try (PreparedStatement selectBalance = con.prepareStatement(balanceQuery)) {

            selectBalance.setString(1, number);
            selectBalance.setString(2, pin);

            ResultSet resultSet = selectBalance.executeQuery();
            balance = resultSet.getInt("balance");

        } catch (SQLException ex) {
            System.out.println("Error at getting balance");
            //rollback?
        }
        return balance;
    }

    private void removeUser(String number, String pin) {
        String removeQuery = "DELETE FROM card WHERE number = ? AND pin = ?";

        try (PreparedStatement removeRecord = con.prepareStatement(removeQuery)) {

            removeRecord.setString(1, number);
            removeRecord.setString(2, pin);
            removeRecord.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error at removing user");
            //rollback?
        }
        System.out.println("\nThe account has been closed!");
    }
}
