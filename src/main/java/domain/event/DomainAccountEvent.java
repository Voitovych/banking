package domain.event;

import org.joda.money.Money;

import static domain.event.AccountEventType.CREDIT;
import static domain.event.AccountEventType.DEBIT;

public class DomainAccountEvent implements AccountEvent {

    private final String transactionId;
    private final String accountId;
    private final AccountEventType type;
    private final Money amount;

    private DomainAccountEvent(String transactionId, String accountId, Money amount, AccountEventType type) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
    }

    public static DomainAccountEvent credit(String transactionId, String accountId, Money amount) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Amount must be positive for CREDIT operation");
        }

        return new DomainAccountEvent(transactionId, accountId, amount, CREDIT);
    }

    public static DomainAccountEvent debit(String transactionId, String accountId, Money amount) {
        if (amount.isPositiveOrZero()) {
            throw new IllegalArgumentException("Amount must be negative for DEBIT operation");
        }

        return new DomainAccountEvent(transactionId, accountId, amount, DEBIT);
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
