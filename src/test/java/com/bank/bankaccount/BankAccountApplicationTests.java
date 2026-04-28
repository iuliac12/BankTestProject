package com.bank.bankaccount;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Teste manuale JUnit 5 scrise de dezvoltator (pentru comparatie cu cele generate de EvoSuite).
 * Acopera scenariile "happy path" principale.
 */
@SpringBootTest
public class BankAccountApplicationTests {
    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Ion Popescu");
        account.open();
    }

    void testOpenAccount() {
        BankAccount acc = new BankAccount("Maria Ionescu");
        assertFalse(acc.isOpen());
        acc.open();
        assertTrue(acc.isOpen());
        assertEquals(0.0, acc.getBalance());
    }
    void testDeposit() {
        account.deposit(500.0);
        assertEquals(500.0, account.getBalance());
        account.deposit(250.0);
        assertEquals(750.0, account.getBalance());
        assertEquals(2, account.getTransactionCount());
    }
    void testWithdrawSuccess() {
        account.deposit(1000.0);
        boolean result = account.withdraw(300.0);
        assertTrue(result);
        assertEquals(700.0, account.getBalance());
    }
    void testWithdrawInsufficientFunds() {
        account.deposit(100.0);
        boolean result = account.withdraw(500.0);
        assertFalse(result);
        assertEquals(100.0, account.getBalance()); // soldul ramane neschimbat
    }
    void testTransfer() {
        BankAccount target = new BankAccount("Ana Dumitrescu");
        target.open();
        account.deposit(1000.0);
        boolean ok = account.transfer(target, 400.0);
        assertTrue(ok);
        assertEquals(600.0, account.getBalance());
        assertEquals(400.0, target.getBalance());
    }
    void testApplyInterest() {
        account.deposit(1000.0);
        account.applyInterest(0.10); // 10%
        assertEquals(1100.0, account.getBalance(), 0.001);
    }
    void testCloseAccount() {
        account.deposit(800.0);
        double finalBal = account.close();
        assertEquals(800.0, finalBal);
        assertFalse(account.isOpen());
    }
    void testDepositOnClosedAccountThrows() {
        account.close();
        assertThrows(IllegalStateException.class, () -> account.deposit(100.0));
    }
    void testWithdrawNegativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-50.0));
    }
    void testNullOwnerThrows() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount(null));
    }
}