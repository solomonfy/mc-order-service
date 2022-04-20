package com.medochemie.ordermanagement.OrderService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medochemie.ordermanagement.OrderService.entity.Agent;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.Response;
import com.medochemie.ordermanagement.OrderService.exception.ApiRequestException;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
import com.medochemie.ordermanagement.OrderService.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static com.google.common.collect.ImmutableMap.of;
import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = {"http://localhost:4200/", "http://localhost:3000/"})
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(Order.class);

    final String agentUrl = "http://MC-AGENT-SERVICE/api/v1/agents/list/";

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;
    private OrderService orderService;
    @Autowired
    private OrderRepository repository;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public ResponseEntity<Response> getOrders() {
        logger.info("Return all orders");
        try {
            Map<String, List<Order>> data = new HashMap<>();
            data.put("orders", orderService.findAllOrders());
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("All orders retrieved")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(data)
                            .build()
            );
        } catch (Exception e) {
            throw new ApiRequestException("Oops no orders found");
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Response> getOrder(@PathVariable String id) {
        logger.info("Returning an order with an id " + id);
        Order order = orderService.findOrderById(id);

        if (order != null) {
            try {
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .message("Returning an order with an id " + id)
                                .data(of("order", orderService.findOrderById(id)))
                                .build()
                );
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("No order found")
                        .data(of())
                        .build()
        );
    }

    @GetMapping("/list/{id}/products")
    public ResponseEntity<?> getProductsForOrder(@PathVariable String id) {
        logger.info("Inside getProductsForOrder method of OrderController, getting all products for an order with id " + id);
        Order order = orderService.findOrderById(id);
        ArrayList productList = (ArrayList) orderService.findProductsForOrder(id);

        if (productList.size() > 0) {
            logger.info(String.format("Retrieved list of products in the order number %s", order.getOrderNumber()));
            try {
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .message(String.format("List of products in the order number %s", order.getOrderNumber()))
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .data(of("products", productList))
                                .build()
                );
            } catch (Exception e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.OK);
            }
        }

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .message("No order found with id " + id)
                        .status(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("No order found")
                        .data(of())
                        .build()
        );
    }


    @GetMapping("/list/agent/{agentId}")
    public ResponseEntity<Response> getOrdersForAnAgent(@PathVariable String agentId) {

        Agent agent = null;
        if (agentId != null) {
            agent = restTemplate.getForObject(agentUrl + agentId, Agent.class);
            logger.info("Retrieving all orders of " + agent.getAgentName());
        }

        Map<String, List<Order>> data = new HashMap<>();
        List<Order> orders = orderService.findAllOrdersByAgentId(agentId);
        String message = "";
        Integer orderCount = orders.size();


        if (orderCount > 0 && agent != null) {
            message = orderCount == 1 ?
                    (orderCount + " order has been retrieved for " + agent.getAgentName() + ".") :
                    (orderCount + " orders have been retrieved for " + agent.getAgentName() + ".");

            data.put("orders", orders);

            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message(message)
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(data)
                            .build()
            );
        }
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .message("No orders found for " + agent.getAgentName())
                        .status(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .data(of())
                        .build()
        );
    }


    @PostMapping("/create-order/{agentId}")
    public ResponseEntity<Response> createOrder(@RequestBody Order order, @PathVariable String agentId) {
        logger.info("Adding a new order...");

        Map<String, Order> data = new HashMap<>();
        String message = "";
        HttpStatus httpStatus = HttpStatus.OK;
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        HttpStatus success = HttpStatus.CREATED;

        ResponseEntity responseEntity = ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .message(message)
                        .status(httpStatus)
                        .statusCode(httpStatus.value())
                        .data(data)
                        .build()
        );

        Agent agent = null;
        try {
            order = orderService.createOrder(order, agentId);

            // this agent object was created to check if Active or not.
            agent = restTemplate.getForObject(agentUrl + agentId, Agent.class);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw e;
        }
        if (!agent.isActive()) {
            logger.info("Agent isn't active, order can't be placed");
            message = "Agent isn't active, order can't be placed";
            httpStatus = badRequest;
            return responseEntity;
        }
        if (order.getAgentId() == null || order == null) {
            message = "No agent found, or order is not complete";
            httpStatus = notFound;
            return responseEntity;
        }

//        "New order " + order.getOrderNumber() + " added for " + agent.getAgentName()
        message = String.format("New order %s", order.getOrderNumber() + " added for %s", agent.getAgentName());
        httpStatus = success;
        data.put("order", order);
        return responseEntity;
    }

    @PutMapping("/update-order/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") String id, @RequestBody Order order) {
        Optional<Order> foundOrder = repository.findById(id);
        logger.info("Updating an order with id " + order.getId());
        if (foundOrder.isPresent()) {
            Order updatedOrder = foundOrder.get();
            updatedOrder.setAmount(order.getAmount());
            updatedOrder.setProductIdsWithQuantities(order.getProductIdsWithQuantities());
            updatedOrder.setShipment(order.getShipment());
            updatedOrder.setCreatedOn(order.getCreatedOn());
            return new ResponseEntity(repository.save(order), HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-order{id}")
    public String deleteOrder(@PathVariable String id) {
        repository.deleteById(id);
        return "Order number " + id + " has been deleted!";
    }

    @GetMapping("/page")
    public Map<String, Object> getAllOrdersInPage(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ) {
        return repository.getAllOrdersInPage(pageNo, pageSize, sortBy);
    }


    @GetMapping(value = "/getAllOrdersByIdList")
    public ResponseEntity<List<Order>> getAllOrderByOrderIdList(@RequestParam List<String> orderIdList) {
        try {
            List<Order> orderListToBeReturned = repository.getOrderListByIdList(orderIdList);
            return new ResponseEntity(orderListToBeReturned, null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}