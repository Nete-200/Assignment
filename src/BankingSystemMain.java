
import banking.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankingSystemMain {

    private static final Scanner input = new Scanner(System.in);

    // Polymorphism: BankAccount references hold different account types.
    private static final ArrayList<BankAccount> accounts =
            new ArrayList<>();

    public static void main(String[] args) {

        // Creating Accounts
        accounts.add(new SavingsAccount("900", "Lerato", 1500.00));
        accounts.add(new CurrentAccount("9001", "Thabo", 3000.00));


        System.out.println(" CONSOLE BANKING SYSTEM");


        boolean running = true;

        while (running) {
            showMenu();
            int choice = readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1:
                        displayAllAccounts();
                        break;
                    case 2:
                        depositMoney();
                        break;
                    case 3:
                        withdrawMoney();
                        break;
                    case 4:
                        transferMoney();
                        break;
                    case 5:
                        calculateInterest();
                        break;
                    case 6:
                        requestLoan();
                        break;
                    case 7:
                        displayAccountDetails();
                        break;
                    case 8:
                        running = false;
                        System.out.println("Thank you for using our bank.");
                        break;
                    default:
                        System.out.println("Invalid menu choice.");
                        TransactionLogger.log(
                                "N/A", "MENU", 0, null,
                                "Rejected: invalid menu choice " + choice);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Transaction failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        input.close();
    }

    private static void showMenu() {
        System.out.println("\n BANK MENU ");
        System.out.println("1. Display all accounts");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Transfer money");
        System.out.println("5. Calculate interest");
        System.out.println("6. Request bank loan");
        System.out.println("7. Display account details");
        System.out.println("8. Exit");
    }

    // Exception handling for invalid numeric menu input.
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine();

            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
                TransactionLogger.log(
                        "N/A", "INPUT", 0, null,
                        "Rejected: invalid numeric menu input");
            }
        }
    }

    private static double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine();

            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount, e.g. 250.00.");
                TransactionLogger.log(
                        "N/A", "INPUT", 0, null,
                        "Rejected: invalid amount input");
            }
        }
    }

    private static String readAccountNumber(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static BankAccount findAccount(String number) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(number)) {
                return account;
            }
        }
        return null;
    }

    private static void displayAllAccounts() {
        System.out.println("\n ALL ACCOUNTS ");

        for (BankAccount account : accounts) {
            account.displayDetails();

        }
    }

    private static void depositMoney() {
        String number = readAccountNumber("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            TransactionLogger.log(number, "DEPOSIT", 0, null,
                    "Rejected: account not found");
            return;
        }

        double amount = readAmount("Enter deposit amount: M");

        try {
            account.deposit(amount);
            TransactionLogger.log(number, "DEPOSIT", amount,
                    account.getBalance(), "Successful");
        } catch (IllegalArgumentException e) {
            TransactionLogger.log(number, "DEPOSIT", amount,
                    account.getBalance(), "Rejected: " + e.getMessage());
            throw e;
        }
    }

    private static void withdrawMoney() {
        String number = readAccountNumber("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            TransactionLogger.log(number, "WITHDRAWAL", 0, null,
                    "Rejected: account not found");
            return;
        }

        double amount = readAmount("Enter withdrawal amount: M");

        try {
            account.withdraw(amount);
            TransactionLogger.log(number, "WITHDRAWAL", amount,
                    account.getBalance(), "Successful");
        } catch (IllegalArgumentException e) {
            TransactionLogger.log(number, "WITHDRAWAL", amount,
                    account.getBalance(), "Rejected: " + e.getMessage());
            throw e;
        }
    }

    private static void transferMoney() {
        String fromNumber = readAccountNumber("From account number: ");
        String toNumber = readAccountNumber("To account number: ");

        BankAccount from = findAccount(fromNumber);
        BankAccount to = findAccount(toNumber);

        double amount = readAmount("Enter transfer amount: M");

        if (from == null || to == null) {
            System.out.println("One or both accounts were not found.");
            TransactionLogger.log(fromNumber, "TRANSFER", amount,
                    from == null ? null : from.getBalance(),
                    "Rejected: account not found");
            return;
        }

        if (from == to) {
            System.out.println("You cannot transfer to the same account.");
            TransactionLogger.log(fromNumber, "TRANSFER", amount,
                    from.getBalance(), "Rejected: same source and destination");
            return;
        }

        try {
            // Validate the source withdrawal before changing either balance.
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "Transfer amount must be greater than zero.");
            }

            if (from instanceof SavingsAccount
                    && from.getBalance() - amount < 100.00) {
                throw new IllegalArgumentException(
                        "Savings account must retain M100.00.");
            }

            if (from instanceof CurrentAccount
                    && from.getBalance() - amount < -2000.00) {
                throw new IllegalArgumentException(
                        "Transfer exceeds the overdraft limit.");
            }

            // Transfer is performed only after the checks pass.
            from.withdraw(amount);
            to.deposit(amount);

            TransactionLogger.log(fromNumber, "TRANSFER OUT", amount,
                    from.getBalance(), "Successful; sent to " + toNumber);
            TransactionLogger.log(toNumber, "TRANSFER IN", amount,
                    to.getBalance(), "Successful; received from " + fromNumber);

            System.out.println("Transfer successful.");
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            TransactionLogger.log(fromNumber, "TRANSFER", amount,
                    from.getBalance(), "Rejected: " + e.getMessage());
        }
    }

    private static void calculateInterest() {
        String number = readAccountNumber("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            TransactionLogger.log(number, "INTEREST", 0, null,
                    "Rejected: account not found");
            return;
        }

        double interest = account.calculateInterest();

        System.out.printf("Calculated interest: M%.2f%n", interest);
        TransactionLogger.log(number, "INTEREST", interest,
                account.getBalance(), "Interest calculated");
    }

    private static void requestLoan() {
        String number = readAccountNumber("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            TransactionLogger.log(number, "LOAN", 0, null,
                    "Rejected: account not found");
            return;
        }

        double amount = readAmount("Enter loan amount: M");

        try {
            // Polymorphism obtain the loan through the interface.
            Loanable loanAccount = (Loanable) account;
            loanAccount.requestLoan(amount);

            TransactionLogger.log(number, "LOAN", amount,
                    account.getBalance(), "Approved");
        } catch (IllegalArgumentException e) {
            TransactionLogger.log(number, "LOAN", amount,
                    account.getBalance(), "Rejected: " + e.getMessage());
            throw e;
        }
    }

    private static void displayAccountDetails() {
        String number = readAccountNumber("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            TransactionLogger.log(number, "ACCOUNT DETAILS", 0, null,
                    "Failed: account not found");
            return;
        }

        account.displayDetails();

        if (account instanceof Loanable) {
            System.out.println("Loan balance: M" +
                    String.format("%.2f",
                            account instanceof SavingsAccount
                                    ? ((SavingsAccount) account).getLoanBalance()
                                    : ((CurrentAccount) account).getLoanBalance()));
        }
    }
}

// final class
final class TransactionLogger {

    private TransactionLogger() {
        // Prevent creating logger objects.
    }

    public static void log(String accountNumber, String type,
                           double amount, Double balance, String result) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String time = LocalDateTime.now().format(formatter);
        String balanceText = balance == null
                ? "N/A"
                : String.format("M%.2f", balance);

        String record = time
                + " | Account: " + accountNumber
                + " | Type: " + type
                + " | Amount: " + String.format("M%.2f", amount)
                + " | Balance: " + balanceText
                + " | Result: " + result
                + System.lineSeparator();

        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            writer.write(record);
        } catch (IOException e) {
            System.out.println(
                    "Could not write transaction log: " + e.getMessage());
        }
    }
}
