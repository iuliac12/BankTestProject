package com.bank.bankaccount;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assert;


/**
 * Teste manuale JUnit 4 scrise de dezvoltator (pentru comparatie cu cele generate de EvoSuite).
 * Acopera scenariile "happy path" principale.
 */
public class BankAccountApplicationTests {
    private BankAccount account;

    @Before
    public void setUp() {
        account = new BankAccount("Ion Popescu");
        account.open();
    }

    @Test
    public void testOpenAccount() {
        BankAccount acc = new BankAccount("Maria Ionescu");
        assertFalse(acc.isOpen());
        acc.open();
        assertTrue(acc.isOpen());
        assertEquals(0.0, acc.getBalance(), 0.001);
    }
    @Test
    public void testDeposit() {
        account.deposit(500.0);
        assertEquals(500.0, account.getBalance(), 0.001);
        account.deposit(250.0);
        assertEquals(750.0, account.getBalance(), 0.001);
        assertEquals(2, account.getTransactionCount());
    }
    @Test
    public void testWithdrawSuccess() {
        account.deposit(1000.0);
        boolean result = account.withdraw(300.0);
        assertTrue(result);
        assertEquals(700.0, account.getBalance(), 0.001);
    }
    @Test
    public void testWithdrawInsufficientFunds() {
        account.deposit(100.0);
        boolean result = account.withdraw(500.0);
        assertFalse(result);
        assertEquals(100.0, account.getBalance(), 0.001); // soldul ramane neschimbat
    }
    @Test
    public void testTransfer() {
        BankAccount target = new BankAccount("Ana Dumitrescu");
        target.open();
        account.deposit(1000.0);
        boolean ok = account.transfer(target, 400.0);
        assertTrue(ok);
        assertEquals(600.0, account.getBalance(), 0.001);
        assertEquals(400.0, target.getBalance(), 0.001);
    }
    @Test
    public void testApplyInterest() {
        account.deposit(1000.0);
        account.applyInterest(0.10); // 10%
        assertEquals(1100.0, account.getBalance(), 0.001);
    }
    @Test
    public void testCloseAccount() {
        account.deposit(800.0);
        double finalBal = account.close();
        assertEquals(800.0, finalBal, 0.001);
        assertFalse(account.isOpen());
    }
    @Test
    public void testDepositOnClosedAccountThrows() {
        account.close();
        try {
            account.deposit(100.0);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }
    @Test
    public void testWithdrawNegativeAmountThrows() {
        try {
            account.withdraw(-50.0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    @Test
    public void testNullOwnerThrows() {
        try {
            new BankAccount(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}