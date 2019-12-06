package domain.command;


import org.joda.money.Money;


public interface OpenAccount {
    Money getInitialBalance();
}
