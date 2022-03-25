package com.medochemie.ordermanagement.OrderService.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "OrderSequenceId")
public class OrderSequenceId {
    // id will be country code e.g. ET;
    @Id
    String id;
    Integer sequence;
}
