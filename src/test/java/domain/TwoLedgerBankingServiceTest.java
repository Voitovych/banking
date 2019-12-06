package domain;

import data.TestOpenAccount;
import data.TestPostTransaction;
import domain.account.Account;
import domain.event.AccountEvent;
import domain.event.AccountEventLog;
import domain.event.DomainAccountEvent;
import domain.exception.AccountNotFoundException;
import domain.exception.CreateAccountException;
import domain.exception.PostTransactionException;
import org.joda.money.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static domain.event.AccountEventType.CREDIT;
import static domain.event.AccountEventType.DEBIT;
import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TwoLedgerBankingServiceTest {

    @Mock
    private AccountEventLog accountEventLog;

    @InjectMocks
    private TwoLedgerBankingService service;

    @BeforeEach
    public void before() {
        accountEventLog = mock(AccountEventLog.class);
        service = new TwoLedgerBankingService(accountEventLog);
    }

    @Test
    void openAccount_shouldCreateAccount() {
        Money initialBalance = Money.of(EUR, new BigDecimal("100"));
        TestOpenAccount command = new TestOpenAccount(initialBalance);

        String accountId = service.openAccount(command);

        ArgumentCaptor<AccountEvent> eventCaptor = ArgumentCaptor.forClass(AccountEvent.class);
        verify(accountEventLog, times(1)).post(eventCaptor.capture());

        AccountEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent.getTransactionId());
        assertEquals(accountId, capturedEvent.getAccountId());
        assertEquals(CREDIT, capturedEvent.getType());
        assertEquals(initialBalance, capturedEvent.getAmount());
    }

    @Test
    void openAccount_shouldThrowExceptionIfAmountNegative() {
        Money initialBalance = Money.of(EUR, new BigDecimal("-100"));
        TestOpenAccount command = new TestOpenAccount(initialBalance);

        Assertions.assertThrows(CreateAccountException.class, () -> {
            service.openAccount(command);
        });
    }

    @Test
    void openAccount_shouldThrowExceptionIfAmountZero() {
        Money initialBalance = Money.of(EUR, BigDecimal.ZERO);
        TestOpenAccount command = new TestOpenAccount(initialBalance);

        Assertions.assertThrows(CreateAccountException.class, () -> {
            service.openAccount(command);
        });
    }

    @Test
    void getAccount_shouldReturnAccount() {
        Money amount = Money.of(EUR, new BigDecimal("100"));
        DomainAccountEvent event = DomainAccountEvent.credit("1", "2", amount);
        when(accountEventLog.getEventsByAccountId("1")).thenReturn(Collections.singletonList(event));

        Account account = service.getAccount("1");

        assertEquals("1", account.getId());
        assertEquals(amount, account.getBalance());
    }

    @Test
    void getAccount_shouldThrowExceptionIfNoEventsFound() {
        when(accountEventLog.getEventsByAccountId("1")).thenReturn(new ArrayList<>());

        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            service.getAccount("1");
        });
    }

    @Test
    void postTransaction_shouldPostTransaction() {
        Money initialBalance = Money.of(EUR, new BigDecimal("100"));

        DomainAccountEvent creditorEvent = DomainAccountEvent.credit("1", "1", initialBalance);
        when(accountEventLog.getEventsByAccountId("1")).thenReturn(Collections.singletonList(creditorEvent));

        DomainAccountEvent debtorEvent = DomainAccountEvent.credit("2", "2", initialBalance);
        when(accountEventLog.getEventsByAccountId("2")).thenReturn(Collections.singletonList(debtorEvent));

        Money transactionAmount = Money.of(EUR, new BigDecimal("100"));
        TestPostTransaction command = new TestPostTransaction("1", "2", transactionAmount);

        service.postTransaction(command);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AccountEvent>> eventCaptor = ArgumentCaptor.forClass(List.class);
        verify(accountEventLog, times(1)).postAll(eventCaptor.capture());

        List<AccountEvent> capturedEvents = eventCaptor.getValue();
        assertEquals(2, capturedEvents.size());

        AccountEvent debit = capturedEvents.get(0);
        assertEquals("2", debit.getAccountId());
        assertEquals(DEBIT, debit.getType());
        assertEquals(transactionAmount.negated(), debit.getAmount());

        AccountEvent credit = capturedEvents.get(1);
        assertEquals("1", credit.getAccountId());
        assertEquals(CREDIT, credit.getType());
        assertEquals(transactionAmount, credit.getAmount());
    }

    @Test
    void postTransaction_shouldThrowExceptionIfNotEnoughFunds() {
        Money initialBalance = Money.of(EUR, new BigDecimal("100"));

        DomainAccountEvent creditorEvent = DomainAccountEvent.credit("1", "1", initialBalance);
        when(accountEventLog.getEventsByAccountId("1")).thenReturn(Collections.singletonList(creditorEvent));

        DomainAccountEvent debtorEvent = DomainAccountEvent.credit("2", "2", initialBalance);
        when(accountEventLog.getEventsByAccountId("2")).thenReturn(Collections.singletonList(debtorEvent));

        Money transactionAmount = Money.of(EUR, new BigDecimal("1000"));
        TestPostTransaction command = new TestPostTransaction("1", "2", transactionAmount);

        Assertions.assertThrows(PostTransactionException.class, () -> {
            service.postTransaction(command);
        });
    }

    @Test
    void postTransaction_shouldThrowExceptionIfDebtorNotFound() {
        Money initialBalance = Money.of(EUR, new BigDecimal("100"));

        DomainAccountEvent creditorEvent = DomainAccountEvent.credit("1", "1", initialBalance);
        when(accountEventLog.getEventsByAccountId("1")).thenReturn(Collections.singletonList(creditorEvent));

        Money transactionAmount = Money.of(EUR, new BigDecimal("1000"));
        TestPostTransaction command = new TestPostTransaction("1", "2", transactionAmount);

        Assertions.assertThrows(PostTransactionException.class, () -> {
            service.postTransaction(command);
        });
    }

    @Test
    void postTransaction_shouldThrowExceptionIfCreditorNotFound() {
        Money initialBalance = Money.of(EUR, new BigDecimal("100"));

        DomainAccountEvent debtorEvent = DomainAccountEvent.credit("1", "2", initialBalance);
        when(accountEventLog.getEventsByAccountId("2")).thenReturn(Collections.singletonList(debtorEvent));

        Money transactionAmount = Money.of(EUR, new BigDecimal("1000"));
        TestPostTransaction command = new TestPostTransaction("1", "2", transactionAmount);

        Assertions.assertThrows(PostTransactionException.class, () -> {
            service.postTransaction(command);
        });
    }

    @Test
    void getAllEvents() {
        List<AccountEvent> mockedEvents = Collections.singletonList(mock(AccountEvent.class));
        when(accountEventLog.getAllEvents()).thenReturn(mockedEvents);

        List<AccountEvent> events = service.getAllEvents();

        assertEquals(mockedEvents, events);
    }

    @Test
    void getEventsByAccountId() {
        List<AccountEvent> mockedEvents = Collections.singletonList(mock(AccountEvent.class));
        when(accountEventLog.getEventsByAccountId("1")).thenReturn(mockedEvents);

        List<AccountEvent> events = service.getEventsByAccountId("1");

        assertEquals(mockedEvents, events);
    }
}