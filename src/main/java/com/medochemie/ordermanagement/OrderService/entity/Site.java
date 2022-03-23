package com.medochemie.ordermanagement.OrderService.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
public class Site {

    private String id;
    private String companyId;
    private String siteName;
    private String siteCode;
    private String address;
    private boolean active;
    private String createdBy;
    private Date createdOn;
    private Date updatedOn;
    private String updatedBy;
}
