public class BankAccount {

    String pin;
    String cardNumber;
    int balance;

    public BankAccount setPin(String pin) {
        this.pin = pin;
        return this;
    }

    public BankAccount setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public BankAccount setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public String getPin() {
        return pin;
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public int getBalance() {
        return balance;
    }

    BankAccount(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
    }


    @Override
    public String toString() {
        return "Your card has been created:\n" +
                "Your card number:\n" + cardNumber + "\n" +
                "Your card PIN:\n" + pin + "\n";
    }

}
