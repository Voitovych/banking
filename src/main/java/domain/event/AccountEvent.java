package domain.event;

import org.joda.money.Money;

public interface AccountEvent {
    String getTransactionId();

    String getAccountId();

    AccountEventType getType();

    Money getAmount();
}
