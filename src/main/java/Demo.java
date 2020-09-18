import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class Demo {


    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        String fileName = "";


        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-fileName")) {
                    fileName = args[i + 1];
                }
            }
        }


        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + fileName);
        Connect.connect(fileName, dataSource);

        Menu menu = new Menu();
        boolean isLogged = false;
        BankAccount bankAccount = null;
        /*** First menu actions ***/
        menu.beforeLoggedInMenuActions();
        while (true) {
            if (!isLogged) {
                int action = sc.nextInt();
                switch (action) {
                    case 1:
                        bankAccount = createNewBankAccount(dataSource);
                        handleMenu(isLogged, menu);
                        break;
                    case 2:
                        isLogged = login(dataSource, bankAccount);
                        handleMenu(isLogged, menu);
                        break;
                    case 0:
                        exit();
                        break;
                }

            } else {

                int actionSecondMenu = sc.nextInt();
                switch (actionSecondMenu) {
                    case 1:
                        getBalance(dataSource, bankAccount);
                        handleMenu(isLogged, menu);
                        break;
                    case 2:
                        addIncome(dataSource, bankAccount);
                        handleMenu(isLogged, menu);
                        break;
                    case 3:
                        transfer(dataSource, bankAccount);
                        handleMenu(isLogged, menu);
                        break;
                    case 4:
                        isLogged = close(dataSource, bankAccount);
                        handleMenu(isLogged, menu);
                        break;
                    case 5:
                        isLogged = LogOut(bankAccount);
                        handleMenu(isLogged, menu);
                        break;
                    case 0:
                        exit();
                        break;
                }
            }
        }
    }

    public static void handleMenu(boolean loggedIn, Menu menu) {
        if (loggedIn) {
            menu.loggedInMenuActions();

        } else {
            menu.beforeLoggedInMenuActions();
        }

    }

    public static void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }


    public static void getBalance(SQLiteDataSource dataSource, BankAccount bankAccount) {
        System.out.println("Balance:" + Connect.getBalance(bankAccount.getCardNumber(), dataSource));
    }

    public static boolean LogOut(BankAccount bankAccount) {
        bankAccount.setPin(null).setCardNumber(null).setBalance(0);
        return false;
    }

    public static BankAccount createNewBankAccount(SQLiteDataSource dataSource) {
        BankAccount bankAccount = AccountFactory.make();
        Connect.insert(bankAccount, dataSource);
        System.out.println(bankAccount);
        return bankAccount;
    }

    public static void addIncome(SQLiteDataSource dataSource, BankAccount bankAccount) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter income:");
        int income = sc.nextInt();
        Connect.addIncome(bankAccount.getCardNumber(), income, dataSource);
        System.out.println("Income was added!");
    }

    public static boolean close(SQLiteDataSource dataSource, BankAccount bankAccount) {
        Connect.delete(bankAccount.getCardNumber(), dataSource);
        bankAccount.setPin(null).setCardNumber(null).setBalance(0);
        System.out.println("The account has been closed!");
        return false;
    }

    public static void transfer(SQLiteDataSource dataSource, BankAccount bankAccount) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Transfer\n" +
                "Enter card number:");
        String toNumber = sc.nextLine();
        if (Connect.selectByCardNumber(toNumber, dataSource) == null) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int amount = sc.nextInt();
            if (Connect.getBalance(bankAccount.getCardNumber(), dataSource) >= amount) {
                Connect.doTransfer(bankAccount.getCardNumber(), toNumber, amount, dataSource);
                System.out.println("Success!");
            } else {
                System.out.println("Not enough money!");
            }
        }
    }

    public static boolean login(SQLiteDataSource dataSource, BankAccount bankAccount) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String numberToProve = sc.nextLine();
        System.out.println("Enter your PIN:");
        String pinToProve = sc.nextLine();
        String lookingNumberByCard = Connect.selectByCardNumber(numberToProve, dataSource);
        String lookingNumberByPin = Connect.selectByPin(pinToProve, dataSource);
        boolean existInDb = false;
        if (lookingNumberByCard != null && lookingNumberByPin != null) {
            if (lookingNumberByCard.equals(lookingNumberByPin)) {
                existInDb = true;
            }
        } else {
            System.out.println("Wrong card number or PIN!");
            return false;
        }
        if (existInDb) {
            bankAccount.setBalance(Connect.getBalance(numberToProve, dataSource)).setCardNumber(Connect.selectByCardNumber(numberToProve, dataSource)).setPin(Connect.selectPinCardNumber(numberToProve, dataSource));
            System.out.println("You have successfully logged in!");
            return true;
        }
        return false;
    }


    static class Connect {


        public static void connect(String fileName, SQLiteDataSource dataSource) {

            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(id INTEGER PRIMARY KEY, number TEXT,pin TEXT, balance INTEGER)");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        public static void insert(BankAccount bankAccount, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        statement.executeUpdate("INSERT INTO card(number,pin,balance) VALUES(" + bankAccount.getCardNumber() + "," + bankAccount.getPin() + "," + bankAccount.getBalance() + ")");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        public static void addIncome(String number, int amount, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        statement.executeUpdate("UPDATE card SET balance= balance + " + amount + " WHERE number =" + number);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        public static void delete(String number, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        statement.execute("DELETE FROM card WHERE number =" + number);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        public static String selectByCardNumber(String cardNumber, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        ResultSet rs = statement.executeQuery("SELECT number FROM card WHERE number =" + cardNumber);

                        while (rs.next()) {
                            return rs.getString("number");

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static String selectPinCardNumber(String cardNumber, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        ResultSet rs = statement.executeQuery("SELECT pin FROM card WHERE number =" + cardNumber);

                        while (rs.next()) {
                            return rs.getString("pin");

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }


        public static String selectByPin(String pin, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        ResultSet rs = statement.executeQuery("SELECT number FROM card WHERE pin =" + pin);
                        while (rs.next()) {
                            return rs.getString("number");

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }


        public static int getBalance(String number, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        ResultSet rs = statement.executeQuery("SELECT balance FROM card WHERE number =" + number);
                        while (rs.next()) {
                            return rs.getInt("balance");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }


        public static void doTransfer(String from, String to, int amount, SQLiteDataSource dataSource) {
            try (Connection conn = dataSource.getConnection()) {
                try (Statement statement = conn.createStatement()) {
                    try {
                        statement.executeUpdate("UPDATE card SET balance= balance +" + amount + " WHERE number =" + to);
                        statement.executeUpdate("UPDATE card SET balance= balance -" + amount + " WHERE number =" + from);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}