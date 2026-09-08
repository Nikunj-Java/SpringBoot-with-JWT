package com.neueda.learning.entity;

public class TransactionResponse {

    private Account account;

    private Transaction transaction;

    public TransactionResponse() {
    }

    public TransactionResponse(Account account, Transaction transaction) {
        this.account = account;
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
