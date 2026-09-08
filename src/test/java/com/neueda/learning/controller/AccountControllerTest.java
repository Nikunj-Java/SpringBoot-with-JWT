package com.neueda.learning.controller;

import com.neueda.learning.dto.AccountRequestDTO;
import com.neueda.learning.dto.AccountResponseDTO;
import com.neueda.learning.entity.Account;
import com.neueda.learning.entity.Transaction;
import com.neueda.learning.entity.TransactionResponse;
import com.neueda.learning.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountControllerTest {

    private AccountService service;
    private RestTemplate restTemplate;
    private AccountController controller;

    @BeforeEach
    void setUp() {
        service = mock(AccountService.class);
        restTemplate = mock(RestTemplate.class);
        controller = new AccountController();
        ReflectionTestUtils.setField(controller, "service", service);
        ReflectionTestUtils.setField(controller, "restTemplate", restTemplate);
    }

    @Test
    void createAccount_shouldReturnCreatedResponse() {
        AccountRequestDTO request = new AccountRequestDTO("Alice", new BigDecimal("2500.00"));
        AccountResponseDTO created = new AccountResponseDTO(1, "Alice", new BigDecimal("2500.00"));
        when(service.createAccount(request)).thenReturn(created);

        ResponseEntity<AccountResponseDTO> response = controller.createAccount(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("Alice", response.getBody().getName());
        assertEquals(new BigDecimal("2500.00"), response.getBody().getBalance());
    }

    @Test
    void getAllAccounts_shouldReturnOkWithAccounts() {
        Account account = new Account();
        account.setId(1);
        account.setName("Bob");
        account.setBalance(new BigDecimal("1500.00"));
        when(service.getAllAccounts()).thenReturn(List.of(account));

        ResponseEntity<List<Account>> response = controller.getAllAccounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Bob", response.getBody().get(0).getName());
    }

    @Test
    void updateAccount_shouldReturnOk() throws AccountNotFoundException {
        AccountRequestDTO request = new AccountRequestDTO("Charlie", new BigDecimal("1800.00"));
        when(service.updateAccountMethod(1, request)).thenReturn("Account updated successfully");

        ResponseEntity<String> response = controller.updateAccount(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account updated successfully", response.getBody());
    }

    @Test
    void deleteAccount_shouldReturnOk() throws AccountNotFoundException {
        when(service.deleteAccount(1)).thenReturn("Account deleted successfully");

        ResponseEntity<String> response = controller.deleteAccount(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account deleted successfully", response.getBody());
    }

    @Test
    void getAccountById_shouldReturnOkWithTransactionResponse() throws AccountNotFoundException {
        Account account = new Account();
        account.setId(10);
        account.setName("Diana");
        account.setBalance(new BigDecimal("3000.00"));
        Transaction transaction = new Transaction(11, 10, "DEPOSIT", 200.00);

        when(service.getAccountById(10)).thenReturn(account);
        when(restTemplate.getForObject(eq("http://localhost:8082/v1/transactions/10"), eq(Transaction.class)))
                .thenReturn(transaction);

        ResponseEntity<TransactionResponse> response = controller.getAccountById(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getAccount().getId());
        assertEquals("DEPOSIT", response.getBody().getTransaction().getType());
    }

    @Test
    void getAccountById_shouldThrowForZeroId() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> controller.getAccountById(0));

        assertEquals("Id Can Not Be Zero!", ex.getMessage());
    }

    @Test
    void getAccountById_shouldThrowWhenTransactionServiceCallFails() throws AccountNotFoundException {
        Account account = new Account();
        account.setId(5);
        account.setName("Eva");
        account.setBalance(new BigDecimal("1200.00"));
        when(service.getAccountById(5)).thenReturn(account);
        when(restTemplate.getForObject(eq("http://localhost:8082/v1/transactions/5"), eq(Transaction.class)))
                .thenThrow(new RestClientException("Service down"));

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> controller.getAccountById(5));

        assertEquals("Unable to fetch transaction details for account ID 5", ex.getMessage());
    }

    @Test
    void getAccountById_shouldThrowWhenTransactionNotFound() throws AccountNotFoundException {
        Account account = new Account();
        account.setId(7);
        account.setName("Frank");
        account.setBalance(new BigDecimal("900.00"));
        when(service.getAccountById(7)).thenReturn(account);
        when(restTemplate.getForObject(eq("http://localhost:8082/v1/transactions/7"), eq(Transaction.class)))
                .thenReturn(null);

        AccountNotFoundException ex =
                assertThrows(AccountNotFoundException.class, () -> controller.getAccountById(7));

        assertEquals("Transaction details for account ID 7 not found", ex.getMessage());
    }
}
