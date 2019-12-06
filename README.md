# Banking Service

### Description
Services allows to create accounts and transfer money between them.

#### Model

* Account - represents agreement between an individual (organisation) and a bank.
* AccountEvent - represents an event that can be applied to the account

**EventSourcing** has been used to build versions of accounts. Assume we have 2 events: opened account with initial
balance of EUR 100.00 and send EUR 25.00 tho the friend. The balance will be restored from the sequence of those events:

* Balance: EUR 0.00 >> applying "CREDIT EUR 100.00"
* Balance: EUR 100.00 >> applying "DEBIT EUR 25.00"
* Balance: EUR 75.00

#### Tech

* Java 8
* Spark 2.8.0
* Maven 3.6.0

### How to run

#### Test
`mvn test`

#### Build
`mvn clean package`

#### Run
`java -jar target/banking-1.0-SNAPSHOT-jar-with-dependencies.jar`

### API

### Open Account
Request
```
POST http://localhost:8080/api/accounts

{
	"initialBalanceInCents": 10000
}
```
Response
```
cc03cb08-8c0e-4461-93d5-e44cb65e53fd

201 CREATED
```

### Get Account
Request
```
GET http://localhost:8080/api/accounts/cc03cb08-8c0e-4461-93d5-e44cb65e53fd
```
Response
```
{
    "id": "cc03cb08-8c0e-4461-93d5-e44cb65e53fd",
    "balance": "EUR100.00"
}

200 OK
```

### Get Account's Transactions
Request
```
GET http://localhost:8080/api/accounts/cc03cb08-8c0e-4461-93d5-e44cb65e53fd
```
Response
```
[
    {
        "transactionId": "db06d211-85d9-42d8-8374-c88808740045",
        "parties": [
            {
                "accountId": "cc03cb08-8c0e-4461-93d5-e44cb65e53fd",
                "side": "CREDIT",
                "amount": "EUR90.00"
            }
        ]
    },
    {
        "transactionId": "716ed7a6-0cae-4b7d-88fb-ca9784deb350",
        "parties": [
            {
                "accountId": "cc03cb08-8c0e-4461-93d5-e44cb65e53fd",
                "side": "CREDIT",
                "amount": "EUR100.00"
            }
        ]
    }
]

200 OK
```

### Post Transaction
Request
```
POST http://localhost:8080/api/transactions

{
	"creditAccountId": "cc03cb08-8c0e-4461-93d5-e44cb65e53fd",
	"debitAccountId": "a5e10af6-10c9-4bbf-80e9-e616ce2ed636",
	"amountInCents": 9000
}
```
Response
```
201 CREATED
```

### Get Transactions
Request
```
GET http://localhost:8080/api/transactions
```
Response
```
[
    {
        "transactionId": "db06d211-85d9-42d8-8374-c88808740045",
        "parties": [
            {
                "accountId": "a5e10af6-10c9-4bbf-80e9-e616ce2ed636",
                "side": "DEBIT",
                "amount": "EUR-90.00"
            },
            {
                "accountId": "cc03cb08-8c0e-4461-93d5-e44cb65e53fd",
                "side": "CREDIT",
                "amount": "EUR90.00"
            }
        ]
    },
    {
        "transactionId": "3a597188-339b-40bf-aede-5b7da6a7409d",
        "parties": [
            {
                "accountId": "a5e10af6-10c9-4bbf-80e9-e616ce2ed636",
                "side": "CREDIT",
                "amount": "EUR100.00"
            }
        ]
    },
    {
        "transactionId": "716ed7a6-0cae-4b7d-88fb-ca9784deb350",
        "parties": [
            {
                "accountId": "cc03cb08-8c0e-4461-93d5-e44cb65e53fd",
                "side": "CREDIT",
                "amount": "EUR100.00"
            }
        ]
    }
]

200 OK
```