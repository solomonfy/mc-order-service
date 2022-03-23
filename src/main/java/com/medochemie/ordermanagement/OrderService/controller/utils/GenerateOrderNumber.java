package com.medochemie.ordermanagement.OrderService.controller.utils;

import com.medochemie.ordermanagement.OrderService.entity.Agent;
import java.util.Calendar;

public class GenerateOrderNumber {

    public static String generateOrderNumber(String countryCode,Agent agent){

        // need to grab the last generated order #
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String name;
        name = firstTwoChars(agent.getAgentName());
        return countryCode + "/" + name +"/" + currentYear;
    }

    public static String firstTwoChars(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

}
