package com.medochemie.ordermanagement.OrderService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agent {

    private String id;
    private String agentName;
    private String agentCode;
//    private String countryId;
    private boolean active;
}
