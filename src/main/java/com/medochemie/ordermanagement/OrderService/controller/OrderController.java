package com.medochemie.ordermanagement.OrderService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.VO.ProductIdsWithQuantity;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.Response;
import com.medochemie.ordermanagement.OrderService.enums.Status;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
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
@CrossOrigin(origins = {"http://localhost:4200/", "http://localhost:3000/"})
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private final Logger LOGGER = Logger.getLogger(String.valueOf(OrderController.class));
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private OrderRepository repository;

    @GetMapping("/list")
    public ResponseEntity<Response> getOrders() {
        LOGGER.info("Return all orders");
        Map<String, List<Order>> data = new HashMap<>();
        data.put("orders", repository.findAll());
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
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Response> getOrder(@PathVariable String id) {
        LOGGER.info("Returning an order with an id " + id);
        Map<String, Optional<Order>> data = new HashMap<>();
        data.put("order", repository.findById(id));
        try {
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .message("Returning an order with an id " + id)
                            .data(data)
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
    public ResponseEntity<Response> getProductsInOrder(@PathVariable String id) {
        Optional<Order> optionalEntity = repository.findById(id);
        Order order = optionalEntity.get();
        LOGGER.info("Returning products found in an order number " + order.getOrderNumber());

        List<Product> productList = new ArrayList();
        List<ProductIdsWithQuantity> productIdsWithQuantities = order.getProductIdsWithQuantities();

        Double total = 0D;
        order.setAmount(total);
        try {
            if (productIdsWithQuantities.toArray().length > 0) {
                for (ProductIdsWithQuantity productIdWithQty : productIdsWithQuantities) {
                    String productId = productIdWithQty.getProductId();
                    Integer productQty = productIdWithQty.getQuantity();
                    Response response = restTemplate.getForObject("http://MC-COMPANY-SERVICE/api/v1/products/list/" + productId, Response.class);
                    Product product = mapper.convertValue(response.getData().values().toArray()[0], Product.class);
                    total += product.getUnitPrice() * productQty;
                    product.setQuantity(productQty);
                    productList.add(product);
                }
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .message("List of products in the order number " + order.getOrderNumber())
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .data(of("products", productList))
                                .build()
                );
            } else {
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .status(HttpStatus.BAD_REQUEST)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .message("No product found in the order!")
                                .data(of())
                                .build()
                );
            }
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


    @PostMapping("/create-order")
    public ResponseEntity<Response> createOrder(@RequestBody Order order) {

        Double total = 0D;
        Date today = new Date();
        String countryCode = "ET";

        if (order.getProductIdsWithQuantities().toArray().length > 0) {
            LOGGER.info("Adding a new order for " + order.getAgent().getAgentName());
            List<ProductIdsWithQuantity> productIdsWithQuantity = order.getProductIdsWithQuantities();

            for (ProductIdsWithQuantity productIdWithQty : productIdsWithQuantity) {
                String productId = productIdWithQty.getProductId();
                Integer productQty = productIdWithQty.getQuantity();

                Response response = restTemplate.getForObject("http://MC-COMPANY-SERVICE/api/v1/products/list/" + productId, Response.class);
                Product product = mapper.convertValue(response.getData().values().toArray()[0], Product.class);
                total += product.getUnitPrice() * productQty;
            }

            order.setOrderNumber(generateOrderNumber(countryCode, order.getAgent()));
            order.setStatus(Status.Draft);
            order.setCreatedOn(today);
            order.setAmount(total);
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("New order has been created, with id " + order.getId())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(of("order", repository.insert(order)))
                            .build()
            );
        } else {
            LOGGER.info("Order can't be created!");
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Order can't be created!")
                            .data(of())
                            .build()
            );
        }
    }


    @PutMapping("/update-order/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") String id, @RequestBody Order order) {
        LOGGER.info("Updating an order with id " + order.getId());
        Optional<Order> foundOrder = repository.findById(id);
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

    @DeleteMapping("/delete/{id}")
    public String deleteOrder(@PathVariable String id) {
        LOGGER.info("Order number " + id + " has been deleted!");
        repository.deleteById(id);
        return "Order number " + id + " has been deleted!";
    }

    @GetMapping("/page")
    public Map<String, Object> getAllOrdersInPage(

            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ) {
        LOGGER.info("Returning in pages");
        return repository.getAllOrdersInPage(pageNo, pageSize, sortBy);
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
//            if(fetchedOrderList.size() > 0) {
//                orderListToBeReturned.addAll(fetchedOrderList);
//            }
//            return new ResponseEntity(orderListToBeReturned, null, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping("/agent/{agentName}")
    public ResponseEntity<Response> getAllOrdersForAgent(@PathVariable String agentName) {
        List<Order> agentOrders = repository.findAllByAgentName(agentName);
        Map<String, List<Order>> data = new HashMap<>();

        try {
            if (agentOrders.size() > 0) {
                data.put("agent orders", agentOrders);
            }
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("List of orders for agent " + agentName)
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(data)
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

    @GetMapping("/agent/{agentName}/{id}")
    public ResponseEntity<Response> getOrderByIdForAgent(@PathVariable String agentName, @PathVariable String id) {
        List<Order> agentOrders = repository.findAllByAgentName(agentName);
        Order order = new Order();
        Map<String, Order> data = new HashMap<>();
        try {
            for (int i = 0; i < agentOrders.size(); i++) {
                if (agentOrders.size() > 0 && agentOrders.get(i).getId().equals(id)) {
                    order = agentOrders.get(i);
                    data.put("order", order);
                }
            }
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .message(agentName + "'s order with id " + id + " is found")
                            .data(data)
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

    @GetMapping("/agent/{agentName}/{id}/products")
    public ResponseEntity<Response> getProductsInAnOrderForAgent(@PathVariable String agentName, @PathVariable String id) {
        List<Order> agentOrders = repository.findAllByAgentName(agentName);
        Optional<Order> optionalOrder = repository.findById(id);
        Order anOrder = optionalOrder.get();
        Map<String, List<Product>> data = new HashMap<>();
        ResponseEntity<Response> response = this.getProductsInOrder(id);

        try {
            if (agentOrders.size() > 0) {
                for (Order order : agentOrders) {
                    if (order.getId().equals(id) && agentOrders.contains(anOrder)) {
                        data = (Map<String, List<Product>>) response.getBody().getData();
                    }
                }
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .message(agentOrders.size() > 0 ? "Products in " + agentName + "'s order with id " + id + " is found" : "No product(s) found in " + agentName + "'s order with id " + id + ".")
                                .data(data)
                                .build()
                );
            } else if (!agentOrders.contains(anOrder)) {
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(now())
                                .status(HttpStatus.BAD_REQUEST)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .message("The order is not from the agent " + agentName)
                                .data(of())
                                .build()
                );
            }
        } catch (Exception e) {

        }
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("No products found")
                        .data(of())
                        .build()
        );

    }


}
