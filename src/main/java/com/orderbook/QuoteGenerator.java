package com.orderbook;

import java.util.Random;

public class QuoteGenerator {
    private static final String[] instruments = new String[] {"BTCUSD", "ETHUSD", "SOLUSD"};

   public static String generateRandomQuote() {
        String time = String.valueOf(System.currentTimeMillis());
        String instrument = getRandomInstrument();
        String price = getRandomPrice();
        String quantity = getRandomQuantity();
        String side = getRandomSide();
        return String.format("t=%s|i=%s|p=%s|q=%s|s=%s", time, instrument, price, quantity, side);
    }

    private static String getRandomInstrument() {
        return instruments[new Random().nextInt(instruments.length)];
    }

    private static String getRandomPrice() {
        double price = new Random().nextDouble(0.01, 999.99);
        boolean flag = new Random().nextBoolean();
        if (flag) {
            return String.format("%.2f", price);
        } else {
            return String.format("%.1f", price);
        }
    }

    private static String getRandomQuantity() {
        double price = new Random().nextDouble(0, 10737418.23);
        boolean flag = new Random().nextBoolean();
        if (flag) {
            return String.format("%.2f", price);
        } else {
            return String.format("%.1f", price);
        }
    }

    private static String getRandomSide() {
        if (new Random().nextBoolean()) {
            return "b";
        }
        return "s";
    }
}
