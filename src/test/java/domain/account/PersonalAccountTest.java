package domain.account;

import domain.event.AccountEvent;
import domain.event.DomainAccountEvent;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.jupiter.api.Assertions.*;

public class PersonalAccountTest {

    @Test
    void fromEvents_shouldCreateAccountWithCorrectBalance() {
        Money initialBalance = Money.of(EUR, new BigDecimal("100"));
        Money credit = Money.of(EUR, new BigDecimal("10"));
        Money debit = Money.of(EUR, new BigDecimal("-25"));

        AccountEvent event1 = DomainAccountEvent.credit("1", "2", initialBalance);
        AccountEvent event2 = DomainAccountEvent.credit("2", "2", credit);
        AccountEvent event3 = DomainAccountEvent.debit("3", "2", debit);

        PersonalAccount account = PersonalAccount.fromEvents("2", Arrays.asList(event1, event2, event3));

        assertEquals("2", account.getId());
        assertEquals(new BigDecimal("85.00"), account.getBalance().getAmount());
    }

    @Test
    void fromEvents_shouldNotCreateAccountWithoutEvents() {
        assertNull(PersonalAccount.fromEvents("2", null));
        assertNull(PersonalAccount.fromEvents("2", new ArrayList<>()));
    }

    @Test
    void canWithdraw() {

        Money balance = Money.of(EUR, new BigDecimal("100"));
        Money withdrawSame = Money.of(EUR, new BigDecimal("100"));
        Money withdrawGreater = Money.of(EUR, new BigDecimal("101"));
        Money withdrawZero = Money.of(EUR, new BigDecimal("0"));
        Money withdrawNegative = Money.of(EUR, new BigDecimal("-10"));

        AccountEvent event = DomainAccountEvent.credit("1", "2", balance);
        PersonalAccount account = PersonalAccount.fromEvents("2", Collections.singletonList(event));

        assertTrue(account.canWithdraw(withdrawSame));
        assertFalse(account.canWithdraw(withdrawGreater));
        assertFalse(account.canWithdraw(withdrawZero));
        assertFalse(account.canWithdraw(withdrawNegative));
    }
}