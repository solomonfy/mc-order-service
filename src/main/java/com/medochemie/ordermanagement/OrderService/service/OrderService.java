package com.medochemie.ordermanagement.OrderService.service;

import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAllOrders();
    Order createOrder(Order order);
    Order findOrderById(String id);
    Order findOrderByOrderNumber(String orderNumber);
    List<Product> findProductsForOrder(String id);
    Order updateOrder(String id);
    String deleteOrder(String id);
}
