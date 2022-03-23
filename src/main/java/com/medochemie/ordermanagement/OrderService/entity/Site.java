package com.medochemie.ordermanagement.OrderService.entity;

import lombok.Data;

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
