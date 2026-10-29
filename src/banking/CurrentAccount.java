
package banking;

// Inheritance and interface implementation.
public class CurrentAccount extends BankAccount implements Loanable {

    private static final double OVERDRAFT_LIMIT = 2000.00;
    private static final double LOAN_LIMIT = 20000.00;

    private double loanBalance;

    public CurrentAccount(String accountNumber,
                          String accountHolder,
                          double openingBalance) {

        super(accountNumber, accountHolder, openingBalance);
        this.loanBalance = 0.0;
    }

    // Overriding current account deposits must be positive.
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

    // Overriding current account
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal must be greater than zero.");
        }

        if (getBalance() - amount < -OVERDRAFT_LIMIT) {
            throw new IllegalArgumentException(
                    "Withdrawal exceeds the M2000 overdraft limit.");
        }

        setBalance(getBalance() - amount);
        System.out.printf("Withdrawal successful. Balance: M%.2f%n",
                getBalance());
    }

    // Current accounts earn no interest.
    @Override
    public double calculateInterest() {
        return 0.0;
    }

    // Interface implementation
    @Override
    public boolean requestLoan(double amount) {
        if (amount <= 0 || amount > LOAN_LIMIT) {
            throw new IllegalArgumentException(
                    "Loan must be positive and cannot exceed M20000.00.");
        }

        loanBalance += amount;
        System.out.printf("Loan approved: M%.2f%n", amount);
        return true;
    }

    public double getLoanBalance() {
        return loanBalance;
    }
}
