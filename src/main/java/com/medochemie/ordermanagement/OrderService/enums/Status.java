package com.medochemie.ordermanagement.OrderService.enums;

public enum Status {

    Draft("Draft"),
    Under_Review("Under Review"),
    Active("Active"),
    Completed("Completed"),
    Rejected("Rejected");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    //    DRAFT(1, "Draft"),
//    UNDER_REVIEW(2, "Under Review"),
//    ACTIVE(3, "Active"),
//    COMPLETED(4, "Completed"),
//    REJECTED(5, "Rejected");
//
//    private final int statusId;
//    private final String displayStatus;
//
//    Status(int statusId, String displayStatus) {
//        this.statusId = statusId;
//        this.displayStatus = displayStatus;
//    }
//
//    public int getStatusId() {
//        return statusId;
//    }
//
//    public String getDisplayStatus() {
//        return displayStatus;
//    }

}
