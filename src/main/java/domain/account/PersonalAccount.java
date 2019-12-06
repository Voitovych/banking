package domain.account;

import domain.event.AccountEvent;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.List;

public class PersonalAccount implements Account {

    private final String id;
    private final Money balance;

    private PersonalAccount(String id, Money balance) {
        this.id = id;
        this.balance = balance;
    }

    public static PersonalAccount fromEvents(String accountId, List<AccountEvent> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }

        Money balance = events.stream()
                .map(AccountEvent::getAmount)
                .reduce(Money.zero(CurrencyUnit.EUR), Money::plus);

        return new PersonalAccount(accountId, balance);
    }

    public boolean canWithdraw(Money amount) {
        if (amount.isNegativeOrZero()) {
            return false;
        }

        return !balance.isLessThan(amount);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Money getBalance() {
        return balance;
    }
}
