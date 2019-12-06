package rest.mapper;

import data.TestAccountEvent;
import domain.event.AccountEvent;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import rest.controller.dto.Transaction;
import rest.controller.dto.TransactionParty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static domain.event.AccountEventType.CREDIT;
import static domain.event.AccountEventType.DEBIT;
import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionMapperTest {

    @Test
    void testFromEvents_shouldCreateTwoTransactions() {
        Money amount = Money.of(EUR, new BigDecimal("2.5"));

        List<AccountEvent> events = Arrays.asList(
                new TestAccountEvent("1", "2", CREDIT, amount),
                new TestAccountEvent("1", "3", DEBIT, amount.negated())
        );

        List<Transaction> transactions = TransactionMapper.fromEvents(events);

        assertEquals(2, transactions.size());

        Transaction transaction1 = transactions.get(0);
        assertEquals("2", transaction1.getTransactionId());
        assertEquals(1, transaction1.getParties().size());

        TransactionParty party1 = transaction1.getParties().get(0);
        assertEquals("1", party1.getAccountId());
        assertEquals("CREDIT", party1.getSide());
        assertEquals("EUR2.50", party1.getAmount());

        Transaction transaction2 = transactions.get(1);
        assertEquals("3", transaction2.getTransactionId());
        assertEquals(1, transaction2.getParties().size());

        TransactionParty party2 = transaction2.getParties().get(0);
        assertEquals("1", party2.getAccountId());
        assertEquals("DEBIT", party2.getSide());
        assertEquals("EUR-2.50", party2.getAmount());
    }

    @Test
    void testFromEvents_shouldGroupInOneTransaction() {
        Money amount = Money.of(EUR, new BigDecimal("2.5"));

        List<AccountEvent> events = Arrays.asList(
                new TestAccountEvent("1", "2", CREDIT, amount),
                new TestAccountEvent("1", "2", DEBIT, amount.negated())
        );

        List<Transaction> transactions = TransactionMapper.fromEvents(events);

        assertEquals(1, transactions.size());
    }
}