package com.medochemie.ordermanagement.OrderService.repository;

import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Repository;
import org.springframework.retry.annotation.Retryable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    @Query(value="{ 'orderNumber' : ?0 }")
    Order findByOrderNumber(String orderNumber);

    @Query(value="{ '_id' : ?0 }")
    List<Product> findProductsForOrder(String _id);

    default Map<String, Object> getAllOrdersInPage(int pageNo, int pageSize, String sortBy) {
        Map<String, Object> response = new HashMap<String, Object>();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.valueOf(sortBy));

        Page<Order> orderPage = findAll(pageable);
        response.put("data", orderPage.getContent());
        response.put("Total No of pages", orderPage.getTotalPages());
        response.put("Total No of elements", orderPage.getTotalElements());
        response.put("Current page", orderPage.getNumber());

        return response;
    };


}
