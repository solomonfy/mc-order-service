package com.medochemie.ordermanagement.OrderService.controller.utils;

import java.text.DecimalFormat;

public class NumbersToWords {
    private static final String ZERO = "zero";
    private static String[] oneToNine = {
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
    };

    private static String[] tenToNinteen = {
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };

    private static String[] dozens = {
            "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    public static String solution(double number) {
        if(number == 0)
            return ZERO;

        return generate(number).trim();
    }

    public static String generate(double number) {

        if(number >= 1000000000) {
            return generate(number / 1000000000) + " billion " + generate(number % 1000000000) ;
        }
        else if(number >= 1000000) {
            return generate(number / 1000000) + " million " + generate(number % 1000000) ;
        }
        else if(number >= 1000) {
            return generate(number / 1000) + " thousand " + generate(number % 1000) ;
        }
        else if(number >= 100) {
            return generate(number / 100) + " hundred " + generate(number % 100) ;
        }

        return generate1To99(number);
    }

    private static String generate1To99(double number) {
        if (number == 0)
            return "";

        if (number <= 9)
            return oneToNine[(int) (number - 1)];
        else if (number <= 19)
            return tenToNinteen[(int) (number % 10)];
        else {
            return dozens[(int) (number / 10 - 1)] + " " + generate1To99(number % 10);
        }
    }

    public static String generateNumberWithDecimalPlaces(double number){
        DecimalFormat df = new DecimalFormat("#");
        String decimal = df.format((number - Math.floor(number)) * 100);

        return generate(number) + " & " + decimal + " cents";
    }
}
