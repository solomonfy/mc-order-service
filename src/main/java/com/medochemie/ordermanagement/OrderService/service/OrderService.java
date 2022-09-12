package com.medochemie.ordermanagement.OrderService.service;

import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAllOrders();
    List<Order> findAllOrdersByAgentId(String agentName);
    Order createOrder(Order order, String agentId);
    Order findOrderById(String id);
    Order findOrderByOrderNumber(String orderNumber);
    List<Product> findProductsForOrder(String id);
    boolean updateOrder(String id, Order order);
    boolean deleteOrder(String id);
}