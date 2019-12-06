package data;

import domain.event.AccountEvent;
import domain.event.AccountEventType;
import org.joda.money.Money;

public class TestAccountEvent implements AccountEvent {
    public final String accountId;
    public final String transactionId;
    public final AccountEventType type;
    public final Money amount;

    public TestAccountEvent(String accountId, String transactionId, AccountEventType type, Money amount) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public AccountEventType getType() {
        return type;
    }

    @Override
    public Money getAmount() {
        return amount;
    }
}
