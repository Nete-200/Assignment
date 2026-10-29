
package banking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Abstraction superclass
public abstract class BankAccount {

    // Encapsulation protect account information.
    private String accountNumber;
    private String accountHolder;
    private double balance;

    // Constructor
    public BankAccount(String accountNumber,
                       String accountHolder,
                       double openingBalance) {

        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = openingBalance;
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    // Controlled method for changing the balance.
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // Abstract methods
    public abstract void deposit(double amount);

    public abstract void withdraw(double amount);

    public abstract double calculateInterest();

    // method
    public void displayDetails() {
        System.out.println("Account number: " + accountNumber);
        System.out.println("Account holder: " + accountHolder);
        System.out.printf("Balance: M%.2f%n", balance);
    }
}
