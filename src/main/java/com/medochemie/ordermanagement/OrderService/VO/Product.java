package com.medochemie.ordermanagement.OrderService.VO;
import com.medochemie.ordermanagement.OrderService.entity.Site;
import com.medochemie.ordermanagement.OrderService.enums.Formulation;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class Product {

    private String id;
    private String chemicalName;
    private String genericName;
    private Formulation formulation;
    private String brandName;
    private String strength;
    private String packSize;
    private Float unitPrice;
    private List<Site> productionSites;
    private boolean active;
    private String imageUrl;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private int quantity;
}
