package application.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import domain.BankingService;
import domain.TwoLedgerBankingService;

public class DomainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BankingService.class).to(TwoLedgerBankingService.class).in(Singleton.class);
    }
}
