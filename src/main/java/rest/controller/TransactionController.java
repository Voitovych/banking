package rest.controller;


import domain.BankingService;
import domain.event.AccountEvent;
import rest.controller.dto.PostTransactionRequest;
import rest.controller.dto.Transaction;
import rest.exception.RequestValidationException;
import rest.mapper.TransactionMapper;
import rest.transformer.Transformer;
import spark.Request;
import spark.Response;

import java.util.List;


public class TransactionController {

    private final BankingService service;
    private final Transformer transformer;

    public TransactionController(BankingService service, Transformer transformer) {
        this.service = service;
        this.transformer = transformer;
    }

    public String postTransaction(Request req, Response res) {
        PostTransactionRequest command = transformer.render(req.body(), PostTransactionRequest.class);
        service.postTransaction(command);

        res.status(201);

        return "";
    }

    public String getAllTransactions(Request req, Response res) {
        List<AccountEvent> events = service.getAllEvents();
        List<Transaction> transactions = TransactionMapper.fromEvents(events);

        return transformer.render(transactions);
    }

    public String getTransactionsByAccountId(Request req, Response res) {
        String accountId = req.params(":accountId");
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new RequestValidationException("Account ID cannot be empty");
        }

        List<AccountEvent> events = service.getEventsByAccountId(accountId);
        List<Transaction> transactions = TransactionMapper.fromEvents(events);

        return transformer.render(transactions);
    }

}
