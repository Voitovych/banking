package rest.controller.dto;

import domain.command.OpenAccount;
import org.joda.money.Money;
import rest.mapper.MoneyConverter;

public class OpenAccountRequest implements OpenAccount {

    private final long initialBalanceInCents;

    public OpenAccountRequest(long initialBalanceInCents) {
        this.initialBalanceInCents = initialBalanceInCents;
    }

    @Override
    public Money getInitialBalance() {
        return MoneyConverter.convertToMoney(initialBalanceInCents);
    }
}
