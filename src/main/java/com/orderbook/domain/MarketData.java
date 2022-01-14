package com.orderbook.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public class MarketData {
    private final ConcurrentSkipListMap<BigDecimal, Quote> bidPriceToQuoteMap = new ConcurrentSkipListMap<>(Collections.reverseOrder());
    private final ConcurrentSkipListMap<BigDecimal, Quote> askPriceToQuoteMap = new ConcurrentSkipListMap<>();

    public void insertQuote(Quote quote) {
        switch (quote.getSide()) {
            case BUY -> insertQuote(quote, bidPriceToQuoteMap);
            case SELL -> insertQuote(quote, askPriceToQuoteMap);
        }
    }

    public BigDecimal averagePriceOverNLevels(int n) {
        List<Quote> asks = getTopNAsks(n);
        List<Quote> bids = getTopNBids(n);
        int itemCount = asks.size() + bids.size();
        if (itemCount == 0) return BigDecimal.ZERO;
        return Stream.concat(asks.stream(),bids.stream())
                .map(Quote::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(itemCount), 8, RoundingMode.HALF_UP);
    }

    public BigDecimal totalQuantityOverNLevels(int n) {
        return Stream.concat(getTopNAsks(n).stream(), getTopNBids(n).stream())
                .map(Quote::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal volumeWeightedPriceOverNLevels(int n) {
        List<Quote> asks = getTopNAsks(n);
        List<Quote> bids = getTopNBids(n);
        BigDecimal totalQuantity = Stream.concat(asks.stream(), bids.stream())
                                        .map(Quote::getQuantity)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (BigDecimal.ZERO.compareTo(totalQuantity) == 0) return BigDecimal.ZERO;
        return Stream.concat(asks.stream(), bids.stream())
                    .map(quote -> quote.getPrice().multiply(quote.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(totalQuantity, 8, RoundingMode.HALF_UP);
    }

    public List<Quote> getBids() {
        return bidPriceToQuoteMap.values().stream().toList();
    }

    public List<Quote> getAsks() {
        return askPriceToQuoteMap.values().stream().toList();
    }

    public Quote getTopAsk() {
        Map.Entry<BigDecimal, Quote> entry = askPriceToQuoteMap.firstEntry();
        if (entry == null) return null;
        return entry.getValue();
    }

    public Quote getTopBid() {
        Map.Entry<BigDecimal, Quote> entry = bidPriceToQuoteMap.firstEntry();
        if (entry == null) return null;
        return entry.getValue();
    }

    private List<Quote> getTopNAsks(int n) {
        return askPriceToQuoteMap.values().stream().limit(n).toList();
    }

    private List<Quote> getTopNBids(int n) {
        return bidPriceToQuoteMap.values().stream().limit(n).toList();
    }

    private void insertQuote(Quote quote, ConcurrentSkipListMap<BigDecimal, Quote> priceToQuoteMap) {
        if (BigDecimal.ZERO.compareTo(quote.getQuantity()) == 0) {
            priceToQuoteMap.remove(quote.getPrice());
        } else if (priceToQuoteMap.containsKey(quote.getPrice())) {
            Quote existingQuote = priceToQuoteMap.get(quote.getPrice());
            existingQuote.addQuantity(quote.getQuantity());
        } else {
            priceToQuoteMap.put(quote.getPrice(), quote);
        }
    }
}
