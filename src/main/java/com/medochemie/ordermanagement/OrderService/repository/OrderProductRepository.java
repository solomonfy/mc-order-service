package com.medochemie.ordermanagement.OrderService.repository;

import com.medochemie.ordermanagement.OrderService.entity.OrderProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends MongoRepository<OrderProduct, String> {
}
