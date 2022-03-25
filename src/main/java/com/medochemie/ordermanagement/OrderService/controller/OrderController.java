package com.medochemie.ordermanagement.OrderService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.VO.ProductIdsWithQuantity;
import com.medochemie.ordermanagement.OrderService.entity.Agent;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.Response;
import com.medochemie.ordermanagement.OrderService.enums.Status;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
import com.medochemie.ordermanagement.OrderService.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Logger;

import static com.google.common.collect.ImmutableMap.of;
import static com.medochemie.ordermanagement.OrderService.controller.utils.GenerateOrderNumber.generateOrderNumber;
import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private final static Logger LOGGER = Logger.getLogger("");

    final String productUrl = "http://MC-COMPANY-SERVICE/api/v1/products/list/";
    final String agentUrl = "http://MC-AGENT-SERVICE/api/v1/agents/list/";

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    OrderService orderService;
    @Autowired
    private OrderRepository repository;

    @GetMapping("/list")
    public ResponseEntity<Response> getOrders() {
        log.info("Return all orders");

        Map<String, List<Order>> data = new HashMap<>();
        data.put("Orders", orderService.findAllOrders());

        try {
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
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Response> getOrder(@PathVariable String id) {
        LOGGER.info("Returning an order with an id " + id);
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
                throw e;
            }
        } else {
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

    }

    @GetMapping("/list/{id}/products")
    public ResponseEntity<Response> getProductsForOrder(@PathVariable String id) {
        log.info("Inside getProductsForOrder method of OrderController, found an order of id " + id);
        Order order = orderService.findOrderById(id);

        List<Product> productList = new ArrayList();
        List<ProductIdsWithQuantity> listOfProductIds = order.getProductIdsWithQuantities();

        if (order != null && listOfProductIds.size() > 0) {
            for (ProductIdsWithQuantity productIdWithQuantity : listOfProductIds) {
                String productId = productIdWithQuantity.getProductId();
                Response response = restTemplate.getForObject(productUrl + productId, Response.class);
                Product product = mapper.convertValue(response.getData().values().toArray()[0], Product.class);
                productList.add(product);
            }
            try {
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .message("List of products in the order id " + order.getOrderNumber())
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .data(of("products", productList))
                                .build()
                );
            } catch (Exception e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        if (order != null && order.getProductIdsWithQuantities().size() == 0) {
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("No products found in the order id " + order.getOrderNumber())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );

        }
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .message("No orders found with id " + id)
                        .status(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("No order found")
                        .data(of())
                        .build()
        );
    }


    @GetMapping("/list/agent-name/{agentName}")
    public ResponseEntity<Response> getOrdersForAnAgent(@PathVariable String agentName) {
        log.info("Retrieving all orders of " + agentName);

        Map<String, List<Order>> data = new HashMap<>();
        List<Order> orders = orderService.findAllOrdersByAgentName(agentName);

        if (orders.size() > 0) {
            data.put("Orders", orders);
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message(orders.size() + " orders of " + agentName + " are retrieved")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(data)
                            .build()
            );
        }
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .message("No orders found for " + agentName)
                        .status(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .data(of())
                        .build()
        );


    }


    @PostMapping("/create-order/{agentId}")
    public ResponseEntity<Response> createOrder(@RequestBody Order order, @PathVariable String agentId) {

        log.info("Adding a new order...");
        Agent agent = restTemplate.getForObject(agentUrl + agentId, Agent.class);
        List<ProductIdsWithQuantity> productIdsWithQuantities = null;
        try {
            productIdsWithQuantities = order.getProductIdsWithQuantities();
        }
        catch (Exception e){
            log.info(e.getMessage());
        }

        Double total = 0D;
        if (agent != null && agent.isActive() && order != null && productIdsWithQuantities.size() > 0) {
            order.setAgent(agent);
            for (ProductIdsWithQuantity productIdWithQuantity : productIdsWithQuantities) {
                String productId = productIdWithQuantity.getProductId();
                Response response = restTemplate.getForObject(productUrl + productId, Response.class);
                Product product = mapper.convertValue(response.getData().values().toArray()[0], Product.class);
                total += (product.getUnitPrice() * productIdWithQuantity.getQuantity());
            }

            order.setAmount(total);

            log.info("Added a new order for " + agent.getAgentName());
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .message("New order " + order.getOrderNumber() + " added for " + agent.getAgentName())
                            .data(of("order", orderService.createOrder(order, agentId)))
                            .build()
            );
        }

        if (!agent.isActive()) {
            log.info("Agent isn't active, order can't be placed");
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("Agent isn't active, order can't be placed")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .data(of())
                            .build()
            );
        }
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .message("No agent found, or order is not complete")
                        .status(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .data(of())
                        .build()
        );

    }

    @PutMapping("/update-order/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") String id, @RequestBody Order order) {
        Optional<Order> foundOrder = repository.findById(id);
        LOGGER.info("Updating an order with id " + order.getId());
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
