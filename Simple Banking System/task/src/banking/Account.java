package banking;

public class Account {
    private int balance;
    private Card card;

    public Account(int balance, Card card) {
        this.balance = balance;
        this.card = card;
    }

    public int getBalance() {
        return balance;
    }

    public Card getCard() {
        return card;
    }
}
