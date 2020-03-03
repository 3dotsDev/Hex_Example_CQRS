package integrationtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static java.math.BigDecimal.*;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountTransactionsIT extends BaseIT {

    @Test
    void returnEmptyTransactions() {
        Response response = resourcesClient.getAccountTransactions(randomUUID().toString());
        ArrayNode transactions = response.readEntity(ArrayNode.class);
        assertThat(transactions.size(), equalTo(0));
        assertThat(response.getStatus(), equalTo(200));
    }

    /**
     * Response schaut folgendermassen aus ...
     * [
     *     {
     *         "accountId": "485eb505-0fc5-4ced-a339-ebc3973aaa9d",
     *         "type": "DEPOSIT",
     *         "amount": 99,
     *         "timestamp": 1583247313144,
     *         "version": 2
     *     },
     *     {
     *         "accountId": "485eb505-0fc5-4ced-a339-ebc3973aaa9d",
     *         "type": "DEPOSIT",
     *         "amount": 1,
     *         "timestamp": 1583247313151,
     *         "version": 3
     *     },
     *     {
     *         "accountId": "485eb505-0fc5-4ced-a339-ebc3973aaa9d",
     *         "type": "WITHDRAWAL",
     *         "amount": 10,
     *         "timestamp": 1583247313162,
     *         "version": 4
     *     }
     * ]
     */
    @Test
    void returnTransactions() {
        String accountId = stateSetup.newAccount(randomUUID().toString());
        resourcesClient.postDeposit(accountId, resourcesDtos.depositDto(valueOf(99))).close();
        resourcesClient.postDeposit(accountId, resourcesDtos.depositDto(ONE)).close();
        resourcesClient.postWithdrawal(accountId, resourcesDtos.withdrawalDto(TEN)).close();

        Response response = resourcesClient.getAccountTransactions(accountId);
        ArrayNode transactions = response.readEntity(ArrayNode.class);
        assertThat(transactions.size(), equalTo(3));
        verifyTransaction(transactions.get(0), "DEPOSIT", 99.0);
        verifyTransaction(transactions.get(1), "DEPOSIT", 1.0);
        verifyTransaction(transactions.get(2), "WITHDRAWAL", 10.0);
        assertThat(response.getStatus(), equalTo(200));
    }

    private void verifyTransaction(JsonNode transaction, String type, double amount) {
        assertThat(transaction.get("type").asText(), equalTo(type));
        assertThat(transaction.get("amount").asDouble(), equalTo(amount));
        assertThat(transaction.get("timestamp"), notNullValue());
    }
}
