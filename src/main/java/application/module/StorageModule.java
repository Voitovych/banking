package application.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import domain.event.AccountEventLog;
import store.InMemoryAccountEventLog;

public class StorageModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountEventLog.class).to(InMemoryAccountEventLog.class).in(Singleton.class);
    }
}
