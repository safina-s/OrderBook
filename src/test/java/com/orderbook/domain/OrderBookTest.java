package com.orderbook.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderBookTest {
    private static final String warning = "Market Data doesn't exist for this instrument";

    @Test
    public void insertQuote_newInstrument_addsQuote(){
        OrderBook orderBook = new OrderBook();
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=100|s=s";
        orderBook.insertQuote(quoteStr);
        assertEquals("0:            | 32.99 100.00\n", orderBook.allAsksAndBids("BTCUSD"));
    }

    @Test
    public void insertQuote_invalidQuote_quoteNotAdded(){
        OrderBook orderBook = new OrderBook();
        String quoteStr = "t=1638848595|i=SOLUSD|p=invalid|q=100|s=s";
        orderBook.insertQuote(quoteStr);
        assertEquals(warning, orderBook.allAsksAndBids("SOLUSD"));
    }

    @Test
    public void allAsksAndBids_noInstrumentExists_returnsWarning() {
        OrderBook orderBook = new OrderBook();
        assertEquals(warning, orderBook.allAsksAndBids("non-existent"));
    }

    @Test
    public void allAsksAndBids_equalAsksAndBids_returnsFormattedString() {
        String firstSellQuoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=160|s=s";
        String secondSellQuoteStr = "t=1638848595|i=BTCUSD|p=34.2|q=170.8|s=s";
        String firstBuyQuoteStr = "t=1638848595|i=BTCUSD|p=37.59|q=1949.5|s=b";
        String secondBuyQuoteStr = "t=1638848595|i=BTCUSD|p=41.6|q=654.56|s=b";
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(firstSellQuoteStr);
        orderBook.insertQuote(secondSellQuoteStr);
        orderBook.insertQuote(firstBuyQuoteStr);
        orderBook.insertQuote(secondBuyQuoteStr);

        String allQuotes = orderBook.allAsksAndBids("BTCUSD");
        String expected = """
                0: 654.56 41.60 | 32.99 160.00
                1: 1949.50 37.59 | 34.20 170.80
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void allAsksAndBids_fewerBids_returnsFormattedString() {
        String firstSellQuoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=160|s=s";
        String secondSellQuoteStr = "t=1638848595|i=BTCUSD|p=34.2|q=170.8|s=s";
        String firstBuyQuoteStr = "t=1638848595|i=BTCUSD|p=37.59|q=1949.5|s=b";
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(firstSellQuoteStr);
        orderBook.insertQuote(secondSellQuoteStr);
        orderBook.insertQuote(firstBuyQuoteStr);

        String allQuotes = orderBook.allAsksAndBids("BTCUSD");
        String expected = """
                0: 1949.50 37.59 | 32.99 160.00
                1:            | 34.20 170.80
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void topLevel_noInstrumentExists_returnsWarning() {
        OrderBook orderBook = new OrderBook();
        assertEquals(warning, orderBook.topLevel("non-existent"));
    }

    @Test
    public void topLevel_dataExists_returnsFormattedString() {
        String firstSellQuoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=160|s=s";
        String secondSellQuoteStr = "t=1638848595|i=BTCUSD|p=34.2|q=170.8|s=s";
        String firstBuyQuoteStr = "t=1638848595|i=BTCUSD|p=37.59|q=1949.5|s=b";
        String secondBuyQuoteStr = "t=1638848595|i=BTCUSD|p=41.6|q=654.56|s=b";
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(firstSellQuoteStr);
        orderBook.insertQuote(secondSellQuoteStr);
        orderBook.insertQuote(firstBuyQuoteStr);
        orderBook.insertQuote(secondBuyQuoteStr);

        String allQuotes = orderBook.topLevel("BTCUSD");
        String expected = """
                0: 654.56 41.60 | 32.99 160.00
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void averagePrice_noInstrumentExists_returnsWarning() {
        OrderBook orderBook = new OrderBook();
        assertEquals(warning, orderBook.averagePrice("non-existent", 5));
    }

    @Test
    public void averagePrice_dataExists_returnsCalcualtedValueAsString() {
        OrderBook orderBook = new OrderBook();
        String quoteStr = "t=1638848595|i=BTCUSD|p=31.99|q=100|s=s";
        orderBook.insertQuote(quoteStr);
        assertEquals("31.99000000", orderBook.averagePrice("BTCUSD", 1));
    }

    @Test
    public void totalQuantity_noInstrumentExists_returnsWarning() {
        OrderBook orderBook = new OrderBook();
        assertEquals(warning, orderBook.totalQuantity("non-existent", 5));
    }

    @Test
    public void totalQuantity_dataExists_returnsCalcualtedValueAsString() {
        OrderBook orderBook = new OrderBook();
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=100|s=s";
        orderBook.insertQuote(quoteStr);
        assertEquals("100.00", orderBook.totalQuantity("BTCUSD", 1));
    }

    @Test
    public void volumeWeightedAveragePrice_noInstrumentExists_returnsWarning() {
        OrderBook orderBook = new OrderBook();
        assertEquals(warning, orderBook.totalQuantity("non-existent", 5));
    }

    @Test
    public void volumeWeightedAveragePrice_dataExists_returnsCalcualtedValueAsString() {
        OrderBook orderBook = new OrderBook();
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=200|s=s";
        orderBook.insertQuote(quoteStr);
        assertEquals("32.99000000", orderBook.volumeWeightedAveragePrice("BTCUSD", 1));
    }
}