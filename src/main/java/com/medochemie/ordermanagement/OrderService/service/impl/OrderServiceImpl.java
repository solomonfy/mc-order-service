package com.medochemie.ordermanagement.OrderService.service.impl;

import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
import com.medochemie.ordermanagement.OrderService.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Order> findAllOrders() {
        return repository.findAll();
    }

    @Override
    public Order createOrder(Order order) {
        return repository.save(order);
    }

    @Override
    public Order findOrderById(String id) {
        return repository.findById(id).get();
    }

    @Override
    public Order findOrderByOrderNumber(String orderNumber) {
        return repository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Product> findProductsForOrder(String id) {
        return repository.findProductsForOrder(id);
    }

    @Override
    public Order updateOrder(String id) {
        Order order = repository.findById(id).get();
        return repository.save(order);
    }

    @Override
    public String deleteOrder(String id) {
        Order existingOrder = repository.findById(id).get();
        if (id != null && existingOrder != null) {
            repository.deleteById(id);
        }
        return "Order with id - " + id + " has been deleted";
    }
}
