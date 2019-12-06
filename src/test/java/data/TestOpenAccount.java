package data;

import domain.command.OpenAccount;
import org.joda.money.Money;

public class TestOpenAccount implements OpenAccount {
    private final Money balance;

    public TestOpenAccount(Money balance) {
        this.balance = balance;
    }

    @Override
    public Money getInitialBalance() {
        return balance;
    }
}
