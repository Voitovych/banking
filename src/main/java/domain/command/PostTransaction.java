package domain.command;


import org.joda.money.Money;

public interface PostTransaction {
    String getCreditAccountId();

    String getDebitAccountId();

    Money getAmount();
}
