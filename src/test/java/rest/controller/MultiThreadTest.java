package rest.controller;

import com.google.gson.reflect.TypeToken;
import domain.BankingService;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import rest.controller.dto.Transaction;
import rest.transformer.Transformer;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static application.Application.INJECTOR;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rest.mapper.MoneyConverter.FORMATTER;

public class MultiThreadTest {
    private final static int NUMBER_OF_ACCOUNTS = 1000;
    private final static int NUMBER_OF_TRANSACTIONS = NUMBER_OF_ACCOUNTS - 1;
    private final static int INITIAL_BALANCE_IN_CENTS = 10000;
    private final static int TRANSACTION_AMOUNT_IN_CENTS = 5000;
    private final static Queue<String> accountIdsQueue = new ConcurrentLinkedQueue<>();
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final Transformer transformer;

    public MultiThreadTest() {
        transformer = INJECTOR.getInstance(Transformer.class);
        BankingService bankingService = INJECTOR.getInstance(BankingService.class);

        accountController = new AccountController(bankingService, transformer);
        transactionController = new TransactionController(bankingService, transformer);
    }

    @Test
    void testMultiThread() throws InterruptedException {
        openAccounts();
        assertEquals(NUMBER_OF_ACCOUNTS, accountIdsQueue.size());

        postTransactions();
        final List<Transaction> transactions = getAllTransactionsRequest();

        final int totalTransactions = NUMBER_OF_ACCOUNTS + NUMBER_OF_TRANSACTIONS;
        assertEquals(totalTransactions, transactions.size());

        final Money expectedBalance = Money.of(
                CurrencyUnit.EUR,
                BigDecimal.valueOf(NUMBER_OF_ACCOUNTS * INITIAL_BALANCE_IN_CENTS, 2)
        );
        final Money totalBalance = calculateTotalBalance(transactions);
        assertEquals(expectedBalance, totalBalance);

    }

    private void openAccounts() throws InterruptedException {
        final Thread[] threads = new Thread[NUMBER_OF_ACCOUNTS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    openAccountRequest();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private void postTransactions() throws InterruptedException {
        final String[] accountIdsArray = new String[NUMBER_OF_ACCOUNTS];
        accountIdsQueue.toArray(accountIdsArray);

        final Thread[] threads = new Thread[NUMBER_OF_TRANSACTIONS];
        for (int i = 0; i < accountIdsArray.length; i++) {
            if (i + 1 >= accountIdsArray.length) {
                break;
            }

            final String debtorId = accountIdsArray[i];
            final String creditorId = accountIdsArray[i + 1];

            threads[i] = new Thread(new Runnable() {
                public void run() {
                    postTransactionRequest(debtorId, creditorId);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private Money calculateTotalBalance(List<Transaction> transactions) {
        final Money[] totalBalance = {Money.zero(CurrencyUnit.EUR)};
        transactions.forEach(t -> {
            t.getParties().forEach(p -> {
                Money m = FORMATTER.parseMoney(p.getAmount());
                totalBalance[0] = totalBalance[0].plus(m);
            });
        });

        return totalBalance[0];
    }

    private void openAccountRequest() {
        final String JSON = format("{ \"initialBalanceInCents\":%d}", INITIAL_BALANCE_IN_CENTS);

        Request req = mock(Request.class);
        when(req.body()).thenReturn(JSON);

        Response res = mock(Response.class);
        final String accountId = accountController.openAccount(req, res);

        accountIdsQueue.add(accountId);
    }

    private void postTransactionRequest(String debtorId, String creditorId) {
        final String JSON = format("{\"creditAccountId\":\"%s\",\"debitAccountId\":\"%s\",\"amountInCents\":%d}",
                debtorId, creditorId, TRANSACTION_AMOUNT_IN_CENTS);

        Request req = mock(Request.class);
        when(req.body()).thenReturn(JSON);

        Response res = mock(Response.class);
        transactionController.postTransaction(req, res);
    }

    private List<Transaction> getAllTransactionsRequest() {
        Request req = mock(Request.class);
        Response res = mock(Response.class);

        final String JSON = transactionController.getAllTransactions(req, res);
        final Type transactionList = new TypeToken<List<Transaction>>() {
        }.getType();

        return transformer.render(JSON, transactionList);
    }
}