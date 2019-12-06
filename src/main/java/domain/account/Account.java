package domain.account;


import org.joda.money.Money;

public interface Account {
    String getId();

    Money getBalance();
}
