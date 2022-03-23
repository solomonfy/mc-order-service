package com.medochemie.ordermanagement.OrderService.entity;

import com.medochemie.ordermanagement.OrderService.VO.Product;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orderProduct")
public class OrderProduct {
    @Id
    private String id;
    private Order order;
    private Product product;
}
