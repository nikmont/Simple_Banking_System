package banking;

import java.util.Random;

public class CardGenerator {

    static Random rand = new Random();

    public static Card create() {
        Card newCard;
        String cardNumber;
        String issuerIdentificationNumber = "400000";
        String customerAccountNumber = genAccountNum();
        int checksum = genChecksum();
        String pin = genPin();

        cardNumber = issuerIdentificationNumber.concat(customerAccountNumber).concat(checksum + "");

        newCard = new Card(cardNumber , pin);
        return newCard;
    }

    private static String genPin() {
        return String.format("%04d", rand.nextInt(10000));
    }

    private static int genChecksum() {
        int checksum = rand.nextInt(10);
        return checksum;
    }

    private static String genAccountNum() {
        return String.format("%09d", rand.nextInt(1000000000));
    }
}
