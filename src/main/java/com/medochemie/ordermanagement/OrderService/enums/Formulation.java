package com.medochemie.ordermanagement.OrderService.enums;


public enum Formulation {
    TABLET(1, "Tablet"),
    CAPSULE(2, "Capsule"),
    SUSPENSION(3, "Suspension"),
    SUPPOSITORY(4, "Suppository"),
    SYRUP(5, "Syrup"),
    GEL(6, "Gel"),
    VAGINAL_SUPPOSITORY(7, "Vaginal Suppository");

    private final int id;
    private final String display;

    private Formulation(int id, String display){
        this.id = id;
        this.display = display;
    }

    public int getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }
}
