package store;

import domain.event.AccountEvent;
import domain.event.AccountEventType;
import org.joda.money.Money;

public class InMemoryAccountEvent implements AccountEvent {
    private final String transactionId;
    private final String accountId;
    private final AccountEventType type;
    private final Money amount;

    public InMemoryAccountEvent(AccountEvent event) {
        this.transactionId = event.getTransactionId();
        this.accountId = event.getAccountId();
        this.type = event.getType();
        this.amount = event.getAmount();
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
