package com.medochemie.ordermanagement.OrderService.controller;

import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.Response;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
import com.medochemie.ordermanagement.OrderService.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    OrderService orderService = Mockito.mock(OrderService.class);
    OrderController orderController = new OrderController(orderService);
    OrderRepository orderRepository;
    ResponseEntity<Response> response;
    List<Order> orderList = new ArrayList<>();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Should return all orders")
    void shouldReturnAllOrders() {
        when(orderService.findAllOrders()).thenReturn(orderList);
        assertEquals(orderList, orderService.findAllOrders());
    }

    @Test
    @DisplayName("Given order id, should return an order")
    void getOrder(String id) {
        Optional<Order> order = orderRepository.findById(id);
//        when(orderService.findOrderById(id)).thenReturn(order);
        assertEquals(order, orderService.findOrderById(id));
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


    //    class Calculator {
//        int multiply(int a, int b) {
//            return a * b;
//        }
//    }
//        Calculator underTest = new Calculator();
//    @Test
//    void shouldMultiplyTwoNumbers() {
//        //given
//        int numberOne = 12;
//        int numberTwo = 3;
//
//        //when
//        int result = underTest.multiply(numberOne, numberTwo);
//
//        //then
//        int expected = 36;
//        assertThat(result).isEqualTo(expected);
//    }

}