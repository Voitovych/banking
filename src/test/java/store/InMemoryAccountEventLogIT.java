package store;

import data.TestAccountEvent;
import domain.event.AccountEvent;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static domain.event.AccountEventType.CREDIT;
import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryAccountEventLogIT {

    private InMemoryAccountEventLog log;

    @BeforeEach
    public void before() {
        log = new InMemoryAccountEventLog();
    }

    @Test
    void post() {
        Money amount = Money.of(EUR, new BigDecimal("100"));
        TestAccountEvent event = new TestAccountEvent("1", "2", CREDIT, amount);

        log.post(event);

        List<AccountEvent> events = log.getAllEvents();
        assertEquals(1, events.size());

        AccountEvent storedEvent = events.get(0);
        assertEquals("1", storedEvent.getAccountId());
        assertEquals("2", storedEvent.getTransactionId());
        assertEquals(CREDIT, storedEvent.getType());
        assertEquals(amount, storedEvent.getAmount());

    }

    @Test
    void postAll() {
        Money amount = Money.of(EUR, new BigDecimal("100"));
        TestAccountEvent event1 = new TestAccountEvent("1", "2", CREDIT, amount);
        TestAccountEvent event2 = new TestAccountEvent("3", "4", CREDIT, amount);

        log.postAll(Arrays.asList(event1, event2));

        List<AccountEvent> events = log.getAllEvents();
        assertEquals(2, events.size());
        assertEquals("2", events.get(0).getTransactionId());
        assertEquals("4", events.get(1).getTransactionId());
    }

    @Test
    void getEventsByAccountId_shouldReturnRelatedEvents() {
        Money amount = Money.of(EUR, new BigDecimal("100"));
        TestAccountEvent event1 = new TestAccountEvent("1", "2", CREDIT, amount);
        TestAccountEvent event2 = new TestAccountEvent("3", "4", CREDIT, amount);

        log.postAll(Arrays.asList(event1, event2));

        List<AccountEvent> events = log.getEventsByAccountId("1");
        assertEquals(1, events.size());
        assertEquals("1", events.get(0).getAccountId());
    }

    @Test
    void getEventsByAccountId_shouldReturnEmptyList() {
        List<AccountEvent> events = log.getEventsByAccountId("1");
        assertTrue(events.isEmpty());
    }
}