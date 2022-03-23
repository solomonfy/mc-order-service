package com.medochemie.ordermanagement.OrderService.VO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.medochemie.ordermanagement.OrderService.entity.Site;
import com.medochemie.ordermanagement.OrderService.enums.Formulation;
import com.medochemie.ordermanagement.OrderService.enums.TherapeuticCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Data
@SuperBuilder
@JsonInclude(NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private String id;
    private String chemicalName;
    private String genericName;
    private String productCode;
    private String brandName;
    private String strength;
    private String packSize;
    private Float unitPrice;
    private Formulation formulation;
    private TherapeuticCategory therapeuticCategory;
    private List<Site> productionSites;
    private List<String> imageUrls;
    private boolean active;
    private String HSCode;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private Integer quantity;

}
