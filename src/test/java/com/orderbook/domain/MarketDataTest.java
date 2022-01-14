package com.orderbook.domain;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MarketDataTest {

    @Test
    public void insertQuote_newSellQuote_insertsIntoAsk() {
        Quote quote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(quote);
        assertEquals(1, marketData.getAsks().size());
        assertEquals(0, BigDecimal.valueOf(32.99).compareTo(marketData.getAsks().get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(marketData.getAsks().get(0).getQuantity()));
        assertEquals(0, marketData.getBids().size());
    }

    @Test
    public void insertQuote_newBuyQuote_insertsIntoBid() {
        Quote quote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=b");
        MarketData marketData = new MarketData();
        marketData.insertQuote(quote);
        assertEquals(1, marketData.getBids().size());
        assertEquals(0, BigDecimal.valueOf(32.99).compareTo(marketData.getBids().get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(marketData.getBids().get(0).getQuantity()));
        assertEquals(0, marketData.getAsks().size());
    }

    @Test
    public void insertQuote_samePrice_aggregatesQuantity() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.8|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.80|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstQuote);
        marketData.insertQuote(secondQuote);
        assertEquals(1, marketData.getAsks().size());
        assertEquals(0, BigDecimal.valueOf(32.8).compareTo(marketData.getAsks().get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(marketData.getAsks().get(0).getQuantity()));
        assertEquals(0, marketData.getBids().size());
    }

    @Test
    public void insertQuote_zeroQuantity_removeQuote() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.8|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.80|q=0|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstQuote);
        marketData.insertQuote(secondQuote);
        assertEquals(0, marketData.getAsks().size());
        assertEquals(0, marketData.getBids().size());
    }

    @Test
    public void getAsks_multipleAsks_returnsAllAsksSortedByLowestPriceAsk() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=39.5|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=35.64|q=100|s=s");
        Quote thirdQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=38.84|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstQuote);
        marketData.insertQuote(secondQuote);
        marketData.insertQuote(thirdQuote);
        List<Quote> asks = marketData.getAsks();
        assertEquals(3, asks.size());
        assertEquals(0, BigDecimal.valueOf(35.64).compareTo(asks.get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(38.84).compareTo(asks.get(1).getPrice()));
        assertEquals(0, BigDecimal.valueOf(39.5).compareTo(asks.get(2).getPrice()));
    }

    @Test
    public void getBids_multipleBids_returnsAllBidsSortedByHighestPriceAsk() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=35.64|q=100|s=b");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=39.5|q=100|s=b");
        Quote thirdQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=38.84|q=100|s=b");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstQuote);
        marketData.insertQuote(secondQuote);
        marketData.insertQuote(thirdQuote);
        List<Quote> bids = marketData.getBids();
        assertEquals(3, bids.size());
        assertEquals(0, BigDecimal.valueOf(39.5).compareTo(bids.get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(38.84).compareTo(bids.get(1).getPrice()));
        assertEquals(0, BigDecimal.valueOf(35.64).compareTo(bids.get(2).getPrice()));
    }

    @Test
    public void getTopAsk_multipleAsks_returnsLowestPriceAsk() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=39.5|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=35.64|q=100|s=s");
        Quote thirdQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=38.84|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstQuote);
        marketData.insertQuote(secondQuote);
        marketData.insertQuote(thirdQuote);
        Quote topAsk = marketData.getTopAsk();
        assertEquals(0, BigDecimal.valueOf(35.64).compareTo(topAsk.getPrice()));
    }

    @Test
    public void getTopAsk_noAsks_returnsNull() {
        MarketData marketData = new MarketData();
        Quote topAsk = marketData.getTopAsk();
        assertNull(topAsk);
    }

    @Test
    public void getTopBid_multipleBids_returnsHighestPriceBid() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=35.64|q=100|s=b");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=39.5|q=100|s=b");
        Quote thirdQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=38.84|q=100|s=b");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstQuote);
        marketData.insertQuote(secondQuote);
        marketData.insertQuote(thirdQuote);
        Quote topBid = marketData.getTopBid();
        assertEquals(0, BigDecimal.valueOf(39.5).compareTo(topBid.getPrice()));
    }

    @Test
    public void getTopBid_noBids_returnsNull() {
        MarketData marketData = new MarketData();
        Quote topBid = marketData.getTopBid();
        assertNull(topBid);
    }

    @Test
    public void averagePriceOverNLevels_sameNumberOfBidsAsks_returnsAveragePrice() {
        Quote firstBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=12.77|q=100|s=b");
        Quote secondBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=14.5|q=100|s=b");
        Quote firstSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=100|s=s");
        Quote secondSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=54.23|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstBuyQuote);
        marketData.insertQuote(secondBuyQuote);
        marketData.insertQuote(firstSellQuote);
        marketData.insertQuote(secondSellQuote);
        BigDecimal averagePrice = marketData.averagePriceOverNLevels(2);
        assertEquals(0, BigDecimal.valueOf(154.02).compareTo(averagePrice));
    }

    @Test
    public void averagePriceOverNLevels_levelsRequestGreaterThanExisting_returnsAveragePrice() {
        Quote firstBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=12.77|q=100|s=b");
        Quote secondBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=14.5|q=100|s=b");
        Quote firstSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=100|s=s");
        Quote secondSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=54.23|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstBuyQuote);
        marketData.insertQuote(secondBuyQuote);
        marketData.insertQuote(firstSellQuote);
        marketData.insertQuote(secondSellQuote);
        BigDecimal averagePrice = marketData.averagePriceOverNLevels(10);
        assertEquals(0, BigDecimal.valueOf(154.02).compareTo(averagePrice));
    }

    @Test
    public void averagePriceOverNLevels_differentNumberOfBidsAndAsks_returnsAveragePrice() {
        Quote firstBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=12.77|q=100|s=b");
        Quote firstSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=100|s=s");
        Quote secondSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=54.23|q=100|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstBuyQuote);
        marketData.insertQuote(firstSellQuote);
        marketData.insertQuote(secondSellQuote);
        BigDecimal averagePrice = marketData.averagePriceOverNLevels(2);
        assertEquals(0, BigDecimal.valueOf(200.52666667).compareTo(averagePrice));
    }

    @Test
    public void averagePriceOverNLevels_noQuotes_returnsZero() {
        MarketData marketData = new MarketData();
        BigDecimal averagePrice = marketData.averagePriceOverNLevels(2);
        assertEquals(0, BigDecimal.ZERO.compareTo(averagePrice));
    }

    @Test
    public void totalQuantityOverNLevels_aggregatedQuote_returnsTotalQuantity() {
        Quote firstBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=12.77|q=160|s=b");
        Quote secondBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=14.5|q=200.67|s=b");
        Quote firstSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=600.5|s=s");
        Quote secondSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=100.99|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstBuyQuote);
        marketData.insertQuote(secondBuyQuote);
        marketData.insertQuote(firstSellQuote);
        marketData.insertQuote(secondSellQuote);
        BigDecimal totalQuantity = marketData.totalQuantityOverNLevels(10);
        assertEquals(0, BigDecimal.valueOf(1062.16).compareTo(totalQuantity));
    }

    @Test
    public void volumeWeightedPriceOverNLevels_aggregatedQuote_returnsTotalQuantity() {
        Quote firstBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=12.77|q=160|s=b");
        Quote secondBuyQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=14.5|q=200.67|s=b");
        Quote firstSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=600.5|s=s");
        Quote secondSellQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=534.58|q=100.99|s=s");
        MarketData marketData = new MarketData();
        marketData.insertQuote(firstBuyQuote);
        marketData.insertQuote(secondBuyQuote);
        marketData.insertQuote(firstSellQuote);
        marketData.insertQuote(secondSellQuote);
        BigDecimal volumeWeightedPrice = marketData.volumeWeightedPriceOverNLevels(2);
        assertEquals(0, BigDecimal.valueOf(357.71958952).compareTo(volumeWeightedPrice));
    }

    @Test
    public void volumeWeightedPriceOverNLevels_noQuotes_returnsZero() {
        MarketData marketData = new MarketData();
        BigDecimal volumeWeightedPrice = marketData.volumeWeightedPriceOverNLevels(2);
        assertEquals(0, BigDecimal.ZERO.compareTo(volumeWeightedPrice));
    }
}