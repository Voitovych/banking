package domain.event;

import java.util.List;

public interface AccountEventLog {
    void post(AccountEvent event);

    void postAll(List<AccountEvent> events);

    List<AccountEvent> getAllEvents();

    List<AccountEvent> getEventsByAccountId(String accountId);
}
