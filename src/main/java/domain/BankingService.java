package domain;

import domain.account.Account;
import domain.command.OpenAccount;
import domain.command.PostTransaction;
import domain.event.AccountEvent;

import java.util.List;

public interface BankingService {

    String openAccount(OpenAccount command);

    Account getAccount(String id);

    void postTransaction(PostTransaction command);

    List<AccountEvent> getAllEvents();

    List<AccountEvent> getEventsByAccountId(String accountId);
}
