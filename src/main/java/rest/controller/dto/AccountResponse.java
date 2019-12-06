package rest.controller.dto;

import domain.account.Account;
import rest.mapper.MoneyConverter;

public class AccountResponse {
    private final String id;
    private final String balance;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.balance = MoneyConverter.convertToString(account.getBalance());
    }

    public String getId() {
        return id;
    }

    public String getBalance() {
        return balance;
    }
}
