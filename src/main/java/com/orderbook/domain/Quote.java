package com.orderbook.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Quote implements Comparable<Quote> {
    private String instrument;
    private BigDecimal price;
    private BigDecimal quantity;
    private Side side;

    public static Quote fromString(String str) throws IllegalArgumentException {
        String[] splitStr = str.split("\\|");
        if (splitStr.length != 5) {
            throw new IllegalArgumentException("Quote is not of the correct format");
        }
        Quote quote = new Quote();
        try {
            quote.instrument = splitStr[1].substring(2);
            quote.price = new BigDecimal(splitStr[2].substring(2)).setScale(2, RoundingMode.HALF_UP);
            quote.quantity = new BigDecimal(splitStr[3].substring(2)).setScale(2, RoundingMode.HALF_UP);
            char side = splitStr[4].substring(2).charAt(0);
            switch (side) {
                case 'b' -> quote.side = Side.BUY;
                case 's' -> quote.side = Side.SELL;
                default -> throw new IllegalArgumentException("Side is not buy/sell");
            }
            return quote;
        } catch(Exception ex) {
            throw new IllegalArgumentException("Quote is not of the correct format", ex);
        }
    }

    public int compareTo(Quote other) {
        return this.price.compareTo(other.price);
    }

    public String getInstrument() {
        return instrument;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Side getSide() {
        return side;
    }

    public void addQuantity(BigDecimal quantity) {
        this.quantity = this.quantity.add(quantity);
    }

    @Override
    public String toString() {
        return switch (side) {
          case BUY -> quantity + " " + price;
          case SELL -> price + " " + quantity;
        };
    }
}