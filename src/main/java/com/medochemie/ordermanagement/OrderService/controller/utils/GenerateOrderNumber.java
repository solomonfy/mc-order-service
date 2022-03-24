package com.medochemie.ordermanagement.OrderService.controller.utils;

import java.util.Calendar;

public class GenerateOrderNumber {

    public static String generateOrderNumber(String countryCode, String agentName) {

        // need to grab the last generated order #
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return firstTwoChars(agentName).toUpperCase() + "/" +
                firstTwoChars(countryCode).toUpperCase() + "/" +
                currentYear;
    }

    public static String firstTwoChars(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

}
