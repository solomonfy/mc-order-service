package com.medochemie.ordermanagement.OrderService.VO;

import com.medochemie.ordermanagement.OrderService.entity.Order;
import lombok.Data;

import java.util.List;


@Data
public class ResponseTemplateVO {
    private Order order;
    private List<Product> products;
}
