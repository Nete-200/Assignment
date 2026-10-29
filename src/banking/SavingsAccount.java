
package banking;

// Inheritance SavingsAccount inherits from BankAccount.
// Interface implementation SavingsAccount implements Loanable.
public class SavingsAccount extends BankAccount implements Loanable {

    private static final double MINIMUM_BALANCE = 100.00;
    private static final double INTEREST_RATE = 0.05;
    private static final double LOAN_LIMIT = 10000.00;

    private double loanBalance;

    // Constructor initializes the savings account.
    public SavingsAccount(String accountNumber,
                          String accountHolder,
                          double openingBalance) {

        super(accountNumber, accountHolder, openingBalance);
        this.loanBalance = 0.0;
    }

    // Method overriding savings account deposit rules.
    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit must be greater than zero.");
        }

        setBalance(getBalance() + amount);
        System.out.printf("Deposit successful. Balance: M%.2f%n",
                getBalance());
    }

    // Method overriding
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal must be greater than zero.");
        }

        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new IllegalArgumentException(
                    "Savings account must retain at least M100.00.");
        }

        setBalance(getBalance() - amount);
        System.out.printf("Withdrawal successful. Balance: M%.2f%n",
                getBalance());
    }

    // Savings accounts earn 5% annual interest.
    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE;
    }

    // Interface implementation
    @Override
    public boolean requestLoan(double amount) {
        if (amount <= 0 || amount > LOAN_LIMIT) {
            throw new IllegalArgumentException(
                    "Loan must be positive and cannot exceed M10000.00.");
        }

        loanBalance += amount;
        System.out.printf("Loan approved: M%.2f%n", amount);
        return true;
    }

    public double getLoanBalance() {
        return loanBalance;
    }
}
