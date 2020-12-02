package banking;

import java.util.Arrays;
import java.util.Random;

public class CardGenerator {

    static Random rand = new Random();

    public static Card create() {
        Card newCard;
        String cardNumber;
        String issuerIdentificationNumber = "400000";

        cardNumber =  genChecksum(issuerIdentificationNumber + genAccountNum());
        String pin = genPin();
        newCard = new Card(cardNumber , pin);
        return newCard;
    }

    private static String genPin() {
        return String.format("%04d", rand.nextInt(10000));
    }

    private static String genChecksum(String number) {
            int[] numbers = Arrays.stream(number.split("")).mapToInt(Integer::parseInt).toArray();
            for (int i = 0; i < numbers.length; i++) {
                if (i % 2 == 0) numbers[i] *= 2;
                if (numbers[i] > 9) numbers[i] -= 9;
            }
            int sum = Arrays.stream(numbers).sum();

            int checksum = 0;
            while ((sum + checksum) % 10 != 0) {
                checksum++;
            }
        return number + checksum;
    }

    private static String genAccountNum() {
        return String.format("%09d", rand.nextInt(1000000000));
    }
}
