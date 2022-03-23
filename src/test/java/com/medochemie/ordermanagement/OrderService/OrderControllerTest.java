//package com.medochemie.ordermanagement.OrderService;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medochemie.ordermanagement.OrderService.controller.OrderController;
//import com.medochemie.ordermanagement.OrderService.entity.Order;
//import com.medochemie.ordermanagement.OrderService.repository.OrderRepository;
//import org.junit.Before;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@DataMongoTest
//class OrderControllerTest {
//
//    @Autowired
//    OrderRepository repository;
//
//    @Autowired
//    MongoTemplate mongoTemplate;
//
//    Calculator underTest = new Calculator();
//
//    @Autowired
//    private OrderController orderController;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.openMocks(this);
//        generateNewOrder();
//    }
//
//    private void generateNewOrder() {
//
//        String orderList = "[{\n" +
//                "    \"_id\": \"1\",\n" +
//                "    \"agent\": {\n" +
//                "                \"agentId\": null,\n" +
//                "                \"agentName\": \"Test\",\n" +
//                "                \"agentCode\": null,\n" +
//                "                \"orders\": null\n" +
//                "            },\n" +
//                "            \"orderNumber\": \"ET/TEST/00003/2022\",\n" +
//                "            \"shipment\": \"Air\",\n" +
//                "            \"productIdsWithQuantities\": [\n" +
//                "                \n" +
//                "            ]\n" +
//                "           \n" +
//                "}, {\n" +
//                "    \"_id\": \"2\",\n" +
//                "    \"agent\": {\n" +
//                "                \"agentId\": null,\n" +
//                "                \"agentName\": \"Test\",\n" +
//                "                \"agentCode\": null,\n" +
//                "                \"orders\": null\n" +
//                "            },\n" +
//                "            \"orderNumber\": \"ET/TEST/00004/2022\",\n" +
//                "            \"shipment\": \"Air\",\n" +
//                "            \"productIdsWithQuantities\": [\n" +
//                "                \n" +
//                "            ]\n" +
//                "           \n" +
//                "}]";
//        try {
//            Order orders[] = new ObjectMapper().readValue(orderList, Order[].class);
//            for (Order order : orders) {
//                repository.save(order);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @DisplayName("given two numbers"
//            + " when multiplied"
//            + " then returns multiple of the two numbers")
//    @Test
//    void shouldMultiplyTwoNumbers() {
//        //given
//        int numberOne = 12;
//        int numberTwo = 3;
//        //when
//        int result = underTest.multiply(numberOne, numberTwo);
//
//        //then
//        int expected = 36;
//        assertThat(result).isEqualTo(expected);
//    }
//
//    @Test
//    void getOrders() {
//        assertTrue(5 > 3);
////        assertArrayEquals(orderController.getOrders);
//    }
//
//    @Test
//    void getOrder() {
//        //given
//        List<Order> list = repository.findAll();
//        for (Order order : list) {
//            assertThat(list.get(0)).isEqualTo(repository.findById("1"));
//        }
//    }
//
//    @Test
//    void returnCustomResponse() {
//        assertThat(true).isTrue();
//    }
//
//    @Test
//    void createOrder() {
//        assertThat(2.0).isGreaterThan(1.2);
//    }
//
//    @Test
//    void updateOrder() {
//        assertThat("Test").contains("e");
//    }
//
//    @Test
//    void deleteOrder() {
//        assertThat("Test").doesNotContain("y");
//    }
//
//    @Test
//    void getAllOrdersInPage() {
//        assertThat("Test").doesNotContain("y");
//    }
//
//    @Test
//    void getAllOrdersForAgent() {
//        assertThat("Test").doesNotContain("y");
//    }
//
//    class Calculator {
//        int multiply(int a, int b) {
//            return a * b;
//        }
//    }
//}