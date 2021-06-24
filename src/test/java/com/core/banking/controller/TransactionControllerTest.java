package com.core.banking.controller;

import static com.core.banking.BankingApplication.SOURCE_ACCOUNT_ID;
import static com.core.banking.BankingApplication.TARGET_ACCOUNT_ID;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.core.banking.dto.TransactionDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerTest {

    @LocalServerPort
    int port;

    @Test
    void shouldTransferSuccessfully() {
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, new BigDecimal(50), "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("message").toString()).isEqualTo("transaction succeeded");
    }

    @Test
    void shouldFailTransferWhenAmountIsZero() {
        BigDecimal amount = new BigDecimal(0);
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, amount, "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo(String.format("Transaction amount (%s) is invalid", amount));
    }

    @Test
    void shouldFailTransferWhenAmountIsNegative() {
        BigDecimal amount = new BigDecimal(-10);
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, amount, "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo(String.format("Transaction amount (%s) is invalid",  amount));
    }

    @Test
    void shouldFailTransferWhenAmountCurrencyIsInvalid() {
        String currency = "EU";
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, new BigDecimal(50), currency))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo(String.format("Transaction currency (%s) is invalid", currency));
    }

    @Test
    void shouldFailTransferWhenSourceAccountIsNotFound() {
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(UUID.randomUUID(), TARGET_ACCOUNT_ID, new BigDecimal(50), "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo("source account not found");
    }

    @Test
    void shouldFailTransferWhenAccountsAreSame() {
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, SOURCE_ACCOUNT_ID, new BigDecimal(50), "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo("source and target accounts are same");
    }

    @Test
    void shouldFailTransferWhenTargetAccountIsNotFound() {
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, UUID.randomUUID(), new BigDecimal(50), "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo("target account not found");
    }

    @Test
    void shouldFailTransferWhenSourceBalanceIsInsufficient() {
        JsonPath result = given()
                .port(port)
                .body(new TransactionDto(SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID, new BigDecimal(500), "GBP"))
                .contentType(ContentType.JSON)
                .post("/api/transactions")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .body()
                .jsonPath();

        assertThat(result.get("error").toString()).isEqualTo("insufficient balance in source account");
    }
}