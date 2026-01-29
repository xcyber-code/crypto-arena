package io.cryptoarena.common;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Crypto Arena BDD Examples")
@Feature("Allure BDD Example")
public class AllureBDDTest {

    @Test
    @Story("Placing an order")
    @DisplayName("Scenario: Given a user with funds, When they place an order, Then the order is accepted")
    @Severity(SeverityLevel.CRITICAL)
    public void placingOrderShouldBeAccepted() {
        givenAUserWithBalance(1000.0);
        whenUserPlacesOrder(250.0);
        thenOrderIsAccepted();
    }

    @Step("Given a user with balance {balance}")
    private void givenAUserWithBalance(double balance) {
        // In a real test, create a user object / service; here we keep state in a static holder for demo
        TestContext.setBalance(balance);
        Allure.addAttachment("Initial balance", String.valueOf(balance));
    }

    @Step("When user places an order of amount {amount}")
    private void whenUserPlacesOrder(double amount) {
        boolean success = OrderService.placeOrder(amount);
        TestContext.setLastOrderPlaced(success);
        Allure.addAttachment("Order amount", String.valueOf(amount));
    }

    @Step("Then the order is accepted")
    private void thenOrderIsAccepted() {
        assertThat(TestContext.wasLastOrderPlaced()).isTrue();
    }

    // --- Simple in-memory helpers for the demo test ---
    static class TestContext {
        private static double balance = 0.0;
        private static boolean lastOrderPlaced = false;

        static void setBalance(double b) { balance = b; }
        static double getBalance() { return balance; }
        static void setLastOrderPlaced(boolean v) { lastOrderPlaced = v; }
        static boolean wasLastOrderPlaced() { return lastOrderPlaced; }
    }

    static class OrderService {
        static boolean placeOrder(double amount) {
            // Very simple logic: accept if balance >= amount
            if (TestContext.getBalance() >= amount) {
                // deduct balance for realism
                TestContext.setBalance(TestContext.getBalance() - amount);
                return true;
            }
            return false;
        }
    }
}
