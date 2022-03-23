package com.medochemie.ordermanagement.OrderService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.VO.ProductIdsWithQuantity;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.Response;
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
import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final static Logger LOGGER = Logger.getLogger("");
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
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(of())
                            .build()
            );
        }
    }

    @GetMapping("/list/{id}/products")
    public ResponseEntity<Response> getProductsForOrder(@PathVariable String id) {

        log.info("Inside getProductsForOrder method of OrderController, found an order of id " + id);
        Optional<Order> optionalEntity = repository.findById(id);
        Order order = optionalEntity.get();

        List<Product> productList = new ArrayList();
        List<ProductIdsWithQuantity> listOfProductIds = order.getProductIdsWithQuantities();

//        Double total = 0D;

        for (ProductIdsWithQuantity productIdWithQuantity : listOfProductIds) {
            String productId = productIdWithQuantity.getProductId();
            Response response = restTemplate.getForObject("http://MC-COMPANY-SERVICE/products/list/" + productId, Response.class);
            Product product = mapper.convertValue(response.getData().values().toArray()[0], Product.class);
//            total += product.getUnitPrice() * productIdWithQuantity.getQuantity();
            productList.add(product);
        }
//        order.setAmount(total);
        try {
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("List of products in the order id " + order.getId())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(of("products", productList))
                            .build()
            );
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping("/list/agent-name/{agentName}")
    public ResponseEntity<Response> getOrdersForAnAgent(@PathVariable String agentName) {
        log.info("Retrieving all orders of " + agentName);

        Map<String, List<Order>> data = new HashMap<>();
        List<Order> orders = orderService.findAllOrdersByAgentName(agentName);
        data.put("Orders", orders);

        if (orders.size() > 0) {
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message(orders.size() + " orders of " + agentName + " are retrieved")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(data)
                            .build()
            );
        } else {
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


    }

    @GetMapping("/page")
    public Map<String, Object> getAllOrdersInPage(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ) {
        return repository.getAllOrdersInPage(pageNo, pageSize, sortBy);
    }

    @PostMapping("/create-order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("Adding a new order for " + order.getAgent().getAgentName());

        return new ResponseEntity(repository.insert(order), HttpStatus.CREATED);
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-order{id}")
    public String deleteOrder(@PathVariable String id) {
        repository.deleteById(id);
        return "Order number " + id + " has been deleted!";
    }


//    @PutMapping("/{update/{id}")
//    public ResponseEntity<?> updateOrder(@RequestBody Order order){
//        try {
//            Order updatedOrder = repository.updateOrder(order);
//            return new ResponseEntity<>(updatedOrder, null, HttpStatus.OK);
//        } catch (Exception e){
//            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
//        }
//    }

//    @GetMapping(value = "/getAllOrdersByIdList")
//    public ResponseEntity<List<Order>> getAllOrderByOrderIdList(@RequestParam("orderIdList") List<String> orderIdList) {
//        try {
//            List<Order> orderListToBeReturned = new ArrayList<Order>();
//            List<Order> fetchedOrderList = repository.getOrderListByIdList(orderIdList);
//
//            if(fetchedOrderList.size() > 0) {
//                orderListToBeReturned.addAll(fetchedOrderList);
//            }
//
//            return new ResponseEntity(orderListToBeReturned, null, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
//        }
//    }
}
