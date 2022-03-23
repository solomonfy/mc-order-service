package com.medochemie.ordermanagement.OrderService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medochemie.ordermanagement.OrderService.VO.Product;
import com.medochemie.ordermanagement.OrderService.VO.ResponseTemplateVO;
import com.medochemie.ordermanagement.OrderService.entity.Order;
import com.medochemie.ordermanagement.OrderService.entity.Response;
import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.DataInput;
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
    private OrderRepository repository;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/list")
    public ResponseEntity<Response> getOrders(){
        log.info("Return all orders");

        Map<String, List<Order>> data = new HashMap<>();
        data.put("Orders", repository.findAll());

        try{
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("All orders retrieved")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(data)
                            .build()
            );
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Response> getOrder(@PathVariable String id){
        LOGGER.info("Returning an order with an id " + id);
        try{
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .message("Returning an order with an id " + id)
                            .data(of("order", repository.findById(id)))
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
    public ResponseEntity<Response> returnCustomResponse(@PathVariable String id){
//        ResponseTemplateVO vo = new ResponseTemplateVO();

        log.info("Inside returnCustomResponse method of OrderController, found an order of id " + id);
        Optional<Order> optionalEntity = repository.findById(id);
        Order order = optionalEntity.get();

        List<Product> productList = new ArrayList();
        List<String> listOfProductIds = order.getProductIds();

        Double total = 0D;

        for(String productId : listOfProductIds) {
            Response response = restTemplate.getForObject("http://MC-COMPANY-SERVICE/products/list/" + productId, Response.class);
            Product product = mapper.convertValue(response.getData().values().toArray()[0], Product.class);
            total += product.getUnitPrice()* product.getQuantity();
            productList.add(product);
        }
        order.setAmount(total);
        try{
            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(now())
                            .message("List of products in the order id " + order.getId())
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(of("products", productList))
                            .build()
            );
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping("/page")
    public Map<String, Object> getAllOrdersInPage(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ){
        return repository.getAllOrdersInPage(pageNo, pageSize, sortBy);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(@RequestBody Order order){
        log.info("Adding a new order for " + order.getAgent().getAgentName());

        return new ResponseEntity(repository.insert(order), HttpStatus.CREATED);
    }

    @PutMapping("/updateOrder/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") String id, @RequestBody Order order){
        Optional<Order> foundOrder = repository.findById(id);
        LOGGER.info("Updating an order with id " + order.getId());
        if (foundOrder.isPresent()){
            Order updatedOrder = foundOrder.get();
            updatedOrder.setAmount(order.getAmount());
            updatedOrder.setProductIds(order.getProductIds());
            updatedOrder.setShipment(order.getShipment());
            updatedOrder.setCreatedOn(order.getCreatedOn());
            return new ResponseEntity(repository.save(order), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping ("/{id}")
    public String deleteOrder(@PathVariable String id){
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
