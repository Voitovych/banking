package rest.controller.dto;

import domain.event.AccountEvent;
import rest.mapper.MoneyConverter;

public class TransactionParty {
    private final String accountId;
    private final String side;
    private final String amount;

    public TransactionParty(AccountEvent event) {
        this.accountId = event.getAccountId();
        this.side = event.getType().toString();
        this.amount = MoneyConverter.convertToString(event.getAmount());
    }

    public String getAccountId() {
        return accountId;
    }

    public String getSide() {
        return side;
    }

    public String getAmount() {
        return amount;
    }
}
