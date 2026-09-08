package com.neueda.learning.repository;

import com.neueda.learning.entity.Account;

import java.util.ArrayList;
import java.util.List;

//@Repository
public class AccountRepository {
    // core business logic
    private final List<Account> accounts= new ArrayList<>();

    // 01. SAVE Account Details
    public Account save(Account account){
        accounts.add(account);
        return account;
    }

    // 02. GET ALL Accounts
    public List<Account> findAll(){
        return accounts;
    }
    // 03. Update
    public String updateAccount(int id,Account account){
        for (Account accnt : accounts){
            if (accnt.getId() == id){
                accnt.setBalance(account.getBalance());
                accnt.setName((account.getName()));
                return "Account updated";
            }
        }
        return "Account not found";
    }
    // 04. Delete
    public String deleteAccount(int id) {

        for (Account account : accounts) {

            if (account.getId() == id) {
                accounts.remove(account);
                return "Account deleted successfully";
            }
        }

        return "Account not found";
    }

    // 05. Get User By Id
    public  Account getAccountById(int id){
        for (Account account : accounts) {
            if (account.getId() == id) {
                return account;
            }
        }
        return null;
    }
}
