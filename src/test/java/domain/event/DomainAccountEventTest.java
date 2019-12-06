package domain.event;

import org.joda.money.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static domain.event.AccountEventType.CREDIT;
import static domain.event.AccountEventType.DEBIT;
import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DomainAccountEventTest {

    @Test
    void fromCredit_shouldCreateValidCredit() {
        Money amount = Money.of(EUR, new BigDecimal("100"));
        DomainAccountEvent event = DomainAccountEvent.credit("1", "2", amount);

        assertEquals("2", event.getAccountId());
        assertEquals("1", event.getTransactionId());
        assertEquals(CREDIT, event.getType());
        assertEquals(amount, event.getAmount());
    }

    @Test
    void fromDebit_shouldCreateValidDebit() {
        Money amount = Money.of(EUR, new BigDecimal("-100"));
        DomainAccountEvent event = DomainAccountEvent.debit("1", "2", amount);

        assertEquals("2", event.getAccountId());
        assertEquals("1", event.getTransactionId());
        assertEquals(DEBIT, event.getType());
        assertEquals(amount, event.getAmount());
    }

    @Test
    void isValid_shouldReturnErrorIfCreditHasNegativeAmount() {
        Money amount = Money.of(EUR, new BigDecimal("-100"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DomainAccountEvent.credit("1", "2", amount);
        });
    }

    @Test
    void isValid_shouldReturnErrorIfCreditHasZeroAmount() {
        Money amount = Money.of(EUR, BigDecimal.ZERO);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DomainAccountEvent.credit("1", "2", amount);
        });
    }

    @Test
    void isValid_shouldReturnErrorIfDebitHasPositiveAmount() {
        Money amount = Money.of(EUR, new BigDecimal("100"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DomainAccountEvent.debit("1", "2", amount);
        });
    }

    @Test
    void isValid_shouldReturnErrorIfDebitHasZeroAmount() {
        Money amount = Money.of(EUR, BigDecimal.ZERO);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DomainAccountEvent.debit("1", "2", amount);
        });
    }
}