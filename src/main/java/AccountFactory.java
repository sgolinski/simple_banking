import java.util.Random;

public class AccountFactory {

    public static BankAccount make() {
        String cardNumber = generateCardNumber().toString();
        String pin = generatePin();

        return new BankAccount(cardNumber, pin);
    }

    private static Long generateCardNumber() {
        Random generator = new Random();
        String cardNumber = "400000";
        while (cardNumber.length() < 15) {
            cardNumber = String.format("%s%s", cardNumber, generator.nextInt(9));
        }
        int controlNumber = calculateControlNumber(cardNumber);

        cardNumber = String.format("%s%s", cardNumber, controlNumber);
        return Long.parseLong(cardNumber);
    }

    private static int calculateControlNumber(String cardNumber) {
        int counter = 0;

        for (int i = 0; i < cardNumber.length(); i++) {
            int number = cardNumber.charAt(i) - '0';
            if (i % 2 == 0) {
                number *= 2;
            }
            if (number > 9) {
                number -= 9;
            }
            counter += number;
        }
        return 10 - (counter % 10);
    }

    private static String generatePin() {
        Random generator = new Random();
        String pin = "";
        while (pin.length() <= 3) {
            int next = generator.nextInt(9);
            if (next > 0) {
                pin = String.format("%s%s", pin, generator.nextInt(9));
            } else {
                next = generator.nextInt(9);
                pin = String.format("%s%s", pin, generator.nextInt(9));
            }

        }
        return pin;
    }
}
