package store;

import domain.event.AccountEvent;
import domain.event.AccountEventLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class InMemoryAccountEventLog implements AccountEventLog {

    private final Queue<InMemoryAccountEvent> log = new ConcurrentLinkedQueue<>();

    @Override
    public void post(AccountEvent event) {
        log.add(new InMemoryAccountEvent(event));
    }

    @Override
    public void postAll(List<AccountEvent> events) {
        events.forEach(this::post);
    }

    @Override
    public List<AccountEvent> getAllEvents() {
        return log.stream().map(InMemoryAccountEvent::new).collect(Collectors.toList());
    }

    @Override
    public List<AccountEvent> getEventsByAccountId(String accountId) {
        if (accountId == null) {
            return new ArrayList<>();
        }

        return log.stream()
                .filter(e -> e.getAccountId().equals(accountId))
                .map(InMemoryAccountEvent::new)
                .collect(Collectors.toList());
    }
}
