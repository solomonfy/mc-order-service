package com.medochemie.ordermanagement.OrderService.service.impl;

import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.OrderSequenceId;
import com.medochemie.ordermanagement.OrderService.enums.Status;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
import com.medochemie.ordermanagement.OrderService.service.OrderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Autowired
    MongoTemplate mongoTemplate;

    public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Order> findAllOrders() {
        return repository.findAll();
    }

    @Override
    public List<Order> findAllOrdersByAgentName(String agentName) {
        return repository.findByAgentName(agentName);
    }

    @Override
    public Order createOrder(Order order, String agentId) {
        Order newOrder = order;
        Order orderFromDB = null;
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

        if (order.getId() != null) {
            orderFromDB = repository.findById(order.getId()).orElse(null);
            order = orderFromDB;
        }

        if (order.getId() == null) {
            order.setOrderNumber(getOrderRefNo(order.getAgent().getAgentCode().trim().toUpperCase()) + "/" + currentYear);
            order.setCreatedOn(new Date());
            order.setStatus(Status.Draft);
            order.setCreatedBy("Logged in user - get from UI");
        }
        try {
            order = repository.save(order);
        } catch (Exception e) {
            order.setId(null);
            order.setOrderNumber(newOrder.getOrderNumber());
            throw e;
        }
        return order;
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


    private String getOrderRefNo(String agentCode) {
        Query query = new Query(Criteria.where("_id").is(agentCode));
        Update update = new Update();
        update.inc("sequence", 1);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        OrderSequenceId orderSequenceId = new OrderSequenceId();
        try {
            orderSequenceId = mongoTemplate.findAndModify(query, update, OrderSequenceId.class);
        } catch (Exception e) {
        }
        String orderRef = "";
        orderRef = agentCode + StringUtils.leftPad(Long.toString(orderSequenceId.getSequence()), 5, "0");
        return orderRef;
    }
}
