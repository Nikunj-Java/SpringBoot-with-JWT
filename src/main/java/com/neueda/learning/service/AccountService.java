package com.neueda.learning.service;

import com.neueda.learning.entity.Account;
import com.neueda.learning.dto.AccountRequestDTO;
import com.neueda.learning.dto.AccountResponseDTO;
import com.neueda.learning.repository.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountMapper repo; //DI of Repo in Service

    // 01. Save Bank Account
    public AccountResponseDTO createAccount(AccountRequestDTO account) {
        Account entity = new Account();
        entity.setName(account.name());
        entity.setBalance(account.balance());
        if (repo.save(entity) != 1) {
            throw new IllegalStateException("Account creation failed");
        }
        return new AccountResponseDTO(entity.getId(), entity.getName(), entity.getBalance());
    }
    // 02. Get All Accounts
    public List<Account> getAllAccounts(){
        return repo.findAll();
    }
    // 03. Update
    public String updateAccountMethod(int id, AccountRequestDTO account) throws AccountNotFoundException {
        Account acc= new Account();
        //c.setId(account.id());
        acc.setName(account.name());
        acc.setBalance(account.balance());
        if (repo.update(id,acc) != 1) {
            throw new AccountNotFoundException("Account with ID " + id + " not found");
        }
        return "Account updated successfully";
    }
    // 04. Delete
    public String deleteAccount(int id) throws AccountNotFoundException {
        if (repo.deleteById(id) != 1) {
            throw new AccountNotFoundException("Account with ID " + id + " not found");
        }
        return "Account deleted successfully";
    }
    // 05. Get Account By Id
    public Account getAccountById(int id) throws AccountNotFoundException {
        Account account = repo.findById(id);
        if (account == null) {
            throw new AccountNotFoundException("Account with ID " + id + " not found");
        }
        return account;
    }

}
