package com.medochemie.ordermanagement.OrderService.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {

    Calculator underTest = new Calculator();

    class Calculator {
        int multiply(int a, int b) {
            return a * b;
        }
    }

    @Test
    void shouldMultiplyTwoNumbers() {
        //given
        int numberOne = 12;
        int numberTwo = 3;

        //when
        int result = underTest.multiply(numberOne, numberTwo);

        //then
        int expected = 36;
        assertThat(result).isEqualTo(expected);
    }


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Should return all orders")
    void getOrders() {
    }

    @Test
    void getOrder() {
    }

    @Test
    void getProductsInOrder() {
    }

    @Test
    void createOrder() {
    }

    @Test
    void updateOrder() {
    }

    @Test
    void deleteOrder() {
    }

    @Test
    void getAllOrdersForAgent() {
    }

    @Test
    void getOrderByIdForAgent() {
    }

    @Test
    void getProductsInAnOrderForAgent() {
    }
}