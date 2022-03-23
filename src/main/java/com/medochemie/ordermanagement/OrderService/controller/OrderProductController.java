package com.medochemie.ordermanagement.OrderService.controller;

import com.medochemie.ordermanagement.OrderService.repository.OrderProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderProductController {
    @Autowired
    private OrderProductRepository OrderProductRepository;
}
