package rest.controller;


import domain.BankingService;
import domain.account.Account;
import rest.controller.dto.AccountResponse;
import rest.controller.dto.OpenAccountRequest;
import rest.exception.RequestValidationException;
import rest.transformer.Transformer;
import spark.Request;
import spark.Response;


public class AccountController {

    private final BankingService service;
    private final Transformer transformer;

    public AccountController(BankingService service, Transformer transformer) {
        this.service = service;
        this.transformer = transformer;
    }

    public String openAccount(Request req, Response res) {
        OpenAccountRequest command = transformer.render(req.body(), OpenAccountRequest.class);
        String accountId = service.openAccount(command);

        res.status(201);

        return accountId;
    }

    public String getAccountById(Request req, Response res) {
        String accountId = req.params(":accountId");
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new RequestValidationException("Account ID cannot be empty");
        }

        Account account = service.getAccount(accountId);
        AccountResponse response = new AccountResponse(account);

        return transformer.render(response);
    }
}
