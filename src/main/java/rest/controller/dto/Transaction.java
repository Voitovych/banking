package rest.controller.dto;

import java.util.List;

public class Transaction {
    private final String transactionId;
    private final List<TransactionParty> parties;

    public Transaction(String transactionId, List<TransactionParty> parties) {
        this.transactionId = transactionId;
        this.parties = parties;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public List<TransactionParty> getParties() {
        return parties;
    }
}
