package com.medochemie.ordermanagement.OrderService.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdsWithQuantity {
    private String productId;
    private Integer quantity;
}
