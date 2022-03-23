package com.medochemie.ordermanagement.OrderService.enums;

public enum TherapeuticCategory {
    ANTI_PAIN(1, "Anti Pain"),
    CARDIO_VASCULAR(2, "Cardiovascular"),
    CNS_DRUGS(3, "CNS Drugs"),
    ANTI_DIABETIC(4, "Anti diabetic");

    private final int id;
    private final String display;

    private TherapeuticCategory(int id, String display){
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
