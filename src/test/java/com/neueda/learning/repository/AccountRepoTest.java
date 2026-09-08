package com.neueda.learning.repository;

import com.neueda.learning.entity.Account;
import com.neueda.learning.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest
@ActiveProfiles("test")
public class AccountRepoTest {

    private AccountRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AccountRepository();
    }

    @Test
    void save_shouldPersistAccountInMemory() {
        Account account = new Account();
        account.setId(1);
        account.setName("Alice");
        account.setBalance(new BigDecimal("1500.00"));

        Account saved = repository.save(account);
        List<Account> allAccounts = repository.findAll();

        assertNotNull(saved);
        assertEquals(1, allAccounts.size());
        assertEquals("Alice", allAccounts.get(0).getName());
        assertEquals(new BigDecimal("1500.00"), allAccounts.get(0).getBalance());
    }

    @Test
    void findAll_shouldReturnAllSavedAccounts() {
        Account first = new Account();
        first.setId(1);
        first.setName("Bob");
        first.setBalance(new BigDecimal("1000.00"));
        Account second = new Account();
        second.setId(2);
        second.setName("Charlie");
        second.setBalance(new BigDecimal("2000.00"));

        repository.save(first);
        repository.save(second);

        List<Account> allAccounts = repository.findAll();

        assertEquals(2, allAccounts.size());
    }

    @Test
    void updateAccount_shouldUpdateExistingAccount() {
        Account existing = new Account();
        existing.setId(10);
        existing.setName("Diana");
        existing.setBalance(new BigDecimal("500.00"));
        repository.save(existing);

        Account updates = new Account();
        updates.setName("Diana Updated");
        updates.setBalance(new BigDecimal("900.00"));

        String result = repository.updateAccount(10, updates);
        Account updated = repository.getAccountById(10);

        assertEquals("Account updated", result);
        assertNotNull(updated);
        assertEquals("Diana Updated", updated.getName());
        assertEquals(new BigDecimal("900.00"), updated.getBalance());
    }

    @Test
    void updateAccount_shouldReturnNotFoundWhenMissingId() {
        Account updates = new Account();
        updates.setName("Eva");
        updates.setBalance(new BigDecimal("1200.00"));

        String result = repository.updateAccount(999, updates);

        assertEquals("Account not found", result);
    }

    @Test
    void deleteAccount_shouldRemoveExistingAccount() {
        Account existing = new Account();
        existing.setId(20);
        existing.setName("Frank");
        existing.setBalance(new BigDecimal("850.00"));
        repository.save(existing);

        String result = repository.deleteAccount(20);
        Account deleted = repository.getAccountById(20);

        assertEquals("Account deleted successfully", result);
        assertNull(deleted);
    }

    @Test
    void deleteAccount_shouldReturnNotFoundWhenMissingId() {
        String result = repository.deleteAccount(888);

        assertEquals("Account not found", result);
    }

    @Test
    void getAccountById_shouldReturnAccountWhenExists() {
        Account account = new Account();
        account.setId(30);
        account.setName("Grace");
        account.setBalance(new BigDecimal("4000.00"));
        repository.save(account);

        Account fetched = repository.getAccountById(30);

        assertNotNull(fetched);
        assertEquals("Grace", fetched.getName());
    }

    @Test
    void getAccountById_shouldReturnNullWhenMissingId() {
        Account fetched = repository.getAccountById(777);

        assertNull(fetched);
    }
}
