package rest.mapper;

import domain.event.AccountEvent;
import rest.controller.dto.Transaction;
import rest.controller.dto.TransactionParty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionMapper {

    public static List<Transaction> fromEvents(List<AccountEvent> events) {

        Map<String, List<AccountEvent>> groupedEvents = events.stream().collect(
                Collectors.groupingBy(AccountEvent::getTransactionId, Collectors.toList())
        );

        List<Transaction> transactions = new ArrayList<>();
        groupedEvents.forEach((transactionId, groupedList) -> {
            List<TransactionParty> parties = groupedList.stream()
                    .map(TransactionParty::new)
                    .collect(Collectors.toList());

            transactions.add(new Transaction(transactionId, parties));
        });

        return transactions;
    }

}
