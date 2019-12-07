package domain;

import com.google.inject.Inject;
import domain.account.Account;
import domain.account.PersonalAccount;
import domain.command.OpenAccount;
import domain.command.PostTransaction;
import domain.event.AccountEvent;
import domain.event.AccountEventLog;
import domain.event.DomainAccountEvent;
import domain.exception.AccountNotFoundException;
import domain.exception.CreateAccountException;
import domain.exception.PostTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;


public class TwoLedgerBankingService implements BankingService {

    private static final Logger log = LoggerFactory.getLogger(TwoLedgerBankingService.class);

    private final AccountEventLog accountEventLog;

    @Inject
    TwoLedgerBankingService(AccountEventLog accountEventLog) {
        this.accountEventLog = accountEventLog;
    }

    @Override
    public synchronized String openAccount(OpenAccount command) {
        try {
            final String transactionId = generateUniqId();
            final String accountId = generateUniqId();
            final DomainAccountEvent event = DomainAccountEvent.credit(transactionId, accountId, command.getInitialBalance());

            accountEventLog.post(event);

            log.info("Created a new account with ID {}", accountId);

            return accountId;
        } catch (Exception e) {
            throw new CreateAccountException(format("Error during opening account: %s", e.getLocalizedMessage()));
        }
    }

    @Override
    public synchronized Account getAccount(String accountId) {
        final List<AccountEvent> events = accountEventLog.getEventsByAccountId(accountId);
        final PersonalAccount account = PersonalAccount.fromEvents(accountId, events);

        if (account == null) {
            throw new AccountNotFoundException(format("Could not find account with ID %s", accountId));
        }

        return PersonalAccount.fromEvents(accountId, events);

    }

    @Override
    public synchronized void postTransaction(PostTransaction command) {
        try {
            final PersonalAccount debtor = (PersonalAccount) getAccount(command.getDebitAccountId());
            final PersonalAccount creditor = (PersonalAccount) getAccount(command.getCreditAccountId());

            if (debtor.getId().equals(creditor.getId())) {
                throw new IllegalArgumentException("Debtor is the same as creditor");
            }

            if (!debtor.canWithdraw(command.getAmount())) {
                throw new IllegalArgumentException(format("Debtor %s has no sufficient funds", debtor.getId()));
            }

            final String transactionId = generateUniqId();
            final DomainAccountEvent debitEvent = DomainAccountEvent.debit(transactionId, debtor.getId(), command.getAmount().negated());
            final DomainAccountEvent creditEvent = DomainAccountEvent.credit(transactionId, creditor.getId(), command.getAmount());

            accountEventLog.postAll(Arrays.asList(debitEvent, creditEvent));

            log.info("{} {} transferred from {} to {}",
                    command.getAmount(),
                    command.getAmount().getCurrencyUnit().getCode(),
                    debtor.getId(), creditor.getId());

        } catch (Exception e) {
            throw new PostTransactionException(format("Transaction Error: %s", e.getLocalizedMessage()));
        }
    }

    @Override
    public synchronized List<AccountEvent> getAllEvents() {
        return accountEventLog.getAllEvents();
    }

    @Override
    public synchronized List<AccountEvent> getEventsByAccountId(String accountId) {
        return accountEventLog.getEventsByAccountId(accountId);
    }

    private String generateUniqId() {
        return UUID.randomUUID().toString();
    }
}
