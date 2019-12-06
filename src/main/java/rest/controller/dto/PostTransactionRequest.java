package rest.controller.dto;

import domain.command.PostTransaction;
import org.joda.money.Money;
import rest.mapper.MoneyConverter;

public class PostTransactionRequest implements PostTransaction {

    private final String creditAccountId;
    private final String debitAccountId;
    private final long amountInCents;

    public PostTransactionRequest(String creditAccountId, String debitAccountId, long amountInCents) {
        this.creditAccountId = creditAccountId;
        this.debitAccountId = debitAccountId;
        this.amountInCents = amountInCents;
    }

    @Override
    public String getCreditAccountId() {
        return creditAccountId;
    }

    @Override
    public String getDebitAccountId() {
        return debitAccountId;
    }

    @Override
    public Money getAmount() {
        return MoneyConverter.convertToMoney(amountInCents);
    }
}
