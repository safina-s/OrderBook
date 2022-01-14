package com.orderbook.domain;

import com.orderbook.QuoteGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrderBook implements Runnable{
    private static final String NO_DATA_STRING = "Market Data doesn't exist for this instrument";
    private final Map<String, MarketData> instrumentMarketData = new HashMap<>();

    @Override
    public void run() {
        readFromFile();
        //generateRandomOrders();
    }

    public String allAsksAndBids(String instrument) {
        if (isNotExistingInstrument(instrument)) return NO_DATA_STRING;
        MarketData marketData = instrumentMarketData.get(instrument);
        return formatBidsAndAsks(marketData.getBids(), marketData.getAsks());
    }

    public String topLevel(String instrument) {
        if (isNotExistingInstrument(instrument)) return NO_DATA_STRING;
        MarketData marketData = instrumentMarketData.get(instrument);
        return formatBidsAndAsks(List.of(marketData.getTopBid()), List.of(marketData.getTopAsk()));
    }

    public String averagePrice(String instrument, int levels) {
        if (isNotExistingInstrument(instrument)) return NO_DATA_STRING;
        MarketData marketData = instrumentMarketData.get(instrument);
        return marketData.averagePriceOverNLevels(levels).toString();
    }

    public String totalQuantity(String instrument, int levels) {
        if (isNotExistingInstrument(instrument)) return NO_DATA_STRING;
        MarketData marketData = instrumentMarketData.get(instrument);
        return marketData.totalQuantityOverNLevels(levels).toString();
    }

    public String volumeWeightedAveragePrice(String instrument, int levels) {
        if (isNotExistingInstrument(instrument)) return NO_DATA_STRING;
        MarketData marketData = instrumentMarketData.get(instrument);
        return marketData.volumeWeightedPriceOverNLevels(levels).toString();
    }

    public void insertQuote(String quoteStr) {
        try {
            Quote quote = Quote.fromString(quoteStr);
            MarketData marketData = instrumentMarketData.computeIfAbsent(quote.getInstrument(), (k) -> new MarketData());
            marketData.insertQuote(quote);
        } catch (IllegalArgumentException ex) {
            System.out.println("Quote cannot be parsed: " + quoteStr);
        }
    }

    private String formatBidsAndAsks(List<Quote> bids, List<Quote> asks) {
        int levels = Integer.max(bids.size(), asks.size());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < levels; i++) {
            sb.append(i).append(": ");
            if (i < bids.size()) {
                sb.append(bids.get(i).toString());
            } else {
                sb.append("          "); // Fill gap with 10 characters for formatting in case fewer bids than asks
            }
            sb.append(" | ");
            if (i < asks.size()) {
                sb.append(asks.get(i).toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private boolean isNotExistingInstrument(String instrument) {
        return !instrumentMarketData.containsKey(instrument);
    }

    private void generateRandomOrders() {
        while (true) {
            try {
                insertQuote(QuoteGenerator.generateRandomQuote());
                Thread.sleep(new Random().nextLong(10, 500));
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    private void readFromFile() {
        try {
            File file = new File(getClass().getClassLoader().getResource("exchange.txt").getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine())!= null)
            {
                insertQuote(line);
                Thread.sleep(new Random().nextLong(10,500));
            }
            reader.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
