package data;

import domain.command.PostTransaction;
import org.joda.money.Money;

public class TestPostTransaction implements PostTransaction {
    private final String creditAccountId;
    private final String debitAccountId;
    private final Money amount;

    public TestPostTransaction(String creditAccountId, String debitAccountId, Money amount) {
        this.creditAccountId = creditAccountId;
        this.debitAccountId = debitAccountId;
        this.amount = amount;
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
        return amount;
    }
}
