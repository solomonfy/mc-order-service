package com.medochemie.ordermanagement.OrderService.controller.utils;

import com.medochemie.ordermanagement.OrderService.entity.OrderSequenceId;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Calendar;
import java.util.Date;

public class GenerateOrderNumber {

    @Autowired
    MongoTemplate mongoTemplate;

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
