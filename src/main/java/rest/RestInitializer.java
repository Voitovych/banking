package rest;

import com.google.inject.Inject;
import domain.BankingService;
import domain.exception.AccountNotFoundException;
import domain.exception.CreateAccountException;
import domain.exception.PostTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.controller.AccountController;
import rest.controller.TransactionController;
import rest.controller.dto.ApiError;
import rest.exception.RequestMalformedException;
import rest.exception.RequestValidationException;
import rest.transformer.Transformer;

import static spark.Spark.*;

public class RestInitializer {

    private static final Logger log = LoggerFactory.getLogger(RestInitializer.class);

    public final BankingService bankingService;
    public final Transformer transformer;

    @Inject
    RestInitializer(BankingService bankingService, Transformer transformer) {
        this.bankingService = bankingService;
        this.transformer = transformer;
    }

    public void initialize() {
        port(8080);

        before((req, res) -> {
            log.info("Received api call -> {} {}", req.requestMethod(), req.uri());
            res.type("application/json");
        });

        initializeApi();
        initializeErrorHandling();
        initializeExceptionHandling();
    }

    private void initializeApi() {
        final AccountController accountController = new AccountController(bankingService, transformer);
        final TransactionController transactionController = new TransactionController(bankingService, transformer);

        path("/api", () -> {
            post("/accounts", accountController::openAccount);
            get("/accounts/:accountId", accountController::getAccountById);
            get("/accounts/:accountId/transactions", transactionController::getTransactionsByAccountId);

            post("/transactions", transactionController::postTransaction);
            get("/transactions", transactionController::getAllTransactions);
        });

    }

    private void initializeErrorHandling() {
        notFound((req, res) -> transformer.render(new ApiError("Not Found")));
        internalServerError((req, res) -> transformer.render(new ApiError("Internal Server Error")));
    }

    private void initializeExceptionHandling() {
        exception(RequestMalformedException.class, (e, req, res) -> {
            res.status(400);
            res.body(transformer.render(new ApiError(e)));
            log.error(e.getLocalizedMessage());
        });

        exception(RequestValidationException.class, (e, req, res) -> {
            res.status(400);
            res.body(transformer.render(new ApiError(e)));
            log.error(e.getLocalizedMessage());
        });

        exception(CreateAccountException.class, (e, req, res) -> {
            res.status(400);
            res.body(transformer.render(new ApiError(e)));
            log.error(e.getLocalizedMessage());
        });

        exception(AccountNotFoundException.class, (e, req, res) -> {
            res.status(404);
            res.body(transformer.render(new ApiError(e)));
            log.error(e.getLocalizedMessage());
        });

        exception(PostTransactionException.class, (e, req, res) -> {
            res.status(400);
            res.body(transformer.render(new ApiError(e)));
            log.error(e.getLocalizedMessage());
        });
    }
}
