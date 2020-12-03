package banking;

public class Main {
    public static void main(String[] args) {
        BankingSystem system = new BankingSystem(args[1]);
        system.run();
    }
}