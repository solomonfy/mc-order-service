package com.medochemie.ordermanagement.OrderService.entity;


import com.medochemie.ordermanagement.OrderService.VO.ProductIdsWithQuantity;
import com.medochemie.ordermanagement.OrderService.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "Order")

public class Order {
    @Id
    private String id;
    private Agent agent;
    private String orderNumber;
    private Double amount;
    private String shipment;
    private Status status;
    private List<ProductIdsWithQuantity> productIdsWithQuantities;

    //    @JsonIgnore
//    @LastModifiedDate
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdOn;
    private String createdBy;

    //    @JsonIgnore
//    @LastModifiedDate
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedOn;
    private String updatedBy;

}
