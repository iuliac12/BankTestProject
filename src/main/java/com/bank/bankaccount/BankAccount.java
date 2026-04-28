package com.bank.bankaccount;

/**
 * BankAccount - Cont bancar simplu pentru demonstrarea EvoSuite.
 * Implementeaza operatiuni de baza: deschidere, depunere, retragere, inchidere.
 */
public class BankAccount {

    public enum AccountState {
        CLOSED, OPEN
    }

    private String owner;
    private double balance;
    private AccountState state;
    private int transactionCount;

    /**
     * Constructor: creeaza un cont inchis cu sold 0.
     */
    public BankAccount(String owner) {
        if (owner == null || owner.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty");
        }
        this.owner = owner;
        this.balance = 0.0;
        this.state = AccountState.CLOSED;
        this.transactionCount = 0;
    }

    /**
     * Deschide contul. Arunca exceptie daca este deja deschis.
     */
    public void open() {
        if (state == AccountState.OPEN) {
            throw new IllegalStateException("Account is already open");
        }
        this.state = AccountState.OPEN;
        this.balance = 0.0;
        this.transactionCount = 0;
    }

    /**
     * Depune o suma pozitiva in cont.
     * @param amount suma de depus (trebuie sa fie > 0)
     */
    public void deposit(double amount) {
        if (state != AccountState.OPEN) {
            throw new IllegalStateException("Cannot deposit: account is not open");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive, got: " + amount);
        }
        this.balance += amount;
        this.transactionCount++;
    }

    /**
     * Retrage o suma din cont daca exista fonduri suficiente.
     * @param amount suma de retras (trebuie sa fie > 0 si <= balance)
     * @return true daca retragerea a reusit, false daca fonduri insuficiente
     */
    public boolean withdraw(double amount) {
        if (state != AccountState.OPEN) {
            throw new IllegalStateException("Cannot withdraw: account is not open");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            return false; // fonduri insuficiente
        }
        this.balance -= amount;
        this.transactionCount++;
        return true;
    }

    /**
     * Transfera o suma catre alt cont.
     * @param target contul destinatie
     * @param amount suma de transferat
     * @return true daca transferul a reusit
     */
    public boolean transfer(BankAccount target, double amount) {
        if (target == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }
        if (target.state != AccountState.OPEN) {
            throw new IllegalStateException("Target account is not open");
        }
        boolean withdrawn = this.withdraw(amount);
        if (withdrawn) {
            target.deposit(amount);
            return true;
        }
        return false;
    }

    /**
     * Aplica dobanda la sold (rata trebuie sa fie intre 0 si 1).
     * @param rate rata dobanzii (ex: 0.05 = 5%)
     */
    public void applyInterest(double rate) {
        if (state != AccountState.OPEN) {
            throw new IllegalStateException("Cannot apply interest: account is not open");
        }
        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException("Interest rate must be between 0 and 1");
        }
        this.balance += this.balance * rate;
    }

    /**
     * Inchide contul si returneaza soldul ramas.
     * @return soldul la inchidere
     */
    public double close() {
        if (state != AccountState.OPEN) {
            throw new IllegalStateException("Cannot close: account is not open");
        }
        double finalBalance = this.balance;
        this.state = AccountState.CLOSED;
        this.balance = 0.0;
        return finalBalance;
    }

    // ---- Getters ----

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public AccountState getState() {
        return state;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public boolean isOpen() {
        return state == AccountState.OPEN;
    }

    @Override
    public String toString() {
        return String.format("BankAccount[owner=%s, state=%s, balance=%.2f, transactions=%d]",
                owner, state, balance, transactionCount);
    }
}