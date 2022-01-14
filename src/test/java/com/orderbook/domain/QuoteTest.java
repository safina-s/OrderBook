package com.orderbook.domain;

import org.junit.Test;

import java.math.BigDecimal;

import static com.orderbook.domain.Side.BUY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class QuoteTest {

    @Test
    public void fromString_validString_returnsQuote() {
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=100|s=b";
        Quote quote = Quote.fromString(quoteStr);
        assertEquals("BTCUSD", quote.getInstrument());
        assertEquals(0, BigDecimal.valueOf(32.99).compareTo(quote.getPrice()));
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(quote.getQuantity()));
        assertEquals(BUY, quote.getSide());
    }

    @Test
    public void fromString_validStringWithReturnCharacter_returnsQuote() {
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=100|s=b\r\n";
        Quote quote = Quote.fromString(quoteStr);
        assertEquals("BTCUSD", quote.getInstrument());
        assertEquals(0, BigDecimal.valueOf(32.99).compareTo(quote.getPrice()));
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(quote.getQuantity()));
        assertEquals(BUY, quote.getSide());
    }

    @Test
    public void fromString_invalidString_throwsException() {
        String quoteStr = "invalid";
        assertThrows(IllegalArgumentException.class, () -> Quote.fromString(quoteStr));
    }

    @Test
    public void fromString_invalidPrice_throwsException() {
        String quoteStr = "t=1638848595|i=BTCUSD|p=invalid|q=100|s=b";
        assertThrows(IllegalArgumentException.class, () -> Quote.fromString(quoteStr));
    }

    @Test
    public void fromString_invalidQuantity_throwsException() {
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=invalid|s=b";
        assertThrows(IllegalArgumentException.class, () -> Quote.fromString(quoteStr));
    }

    @Test
    public void fromString_invalidSide_throwsException() {
        String quoteStr = "t=1638848595|i=BTCUSD|p=32.99|q=100|s=invalid";
        assertThrows(IllegalArgumentException.class, () -> Quote.fromString(quoteStr));
    }

    @Test
    public void toString_buyQuote_quantityFirst() {
        Quote quote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=b");
        assertEquals("100.00 32.99", quote.toString());
    }

    @Test
    public void toString_sellQuote_priceFirst() {
        Quote quote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=s");
        assertEquals("32.99 100.00", quote.toString());
    }

    @Test
    public void addQuantity_quantityAggregated() {
        Quote quote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=s");
        quote.addQuantity(BigDecimal.valueOf(100));
        assertEquals(0, BigDecimal.valueOf(200).compareTo(quote.getQuantity()));
    }

    @Test
    public void compareTo_equalPrice_returnsZero() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=2638848595|i=BTCUSD|p=32.99|q=300|s=s");
        assertEquals(0, firstQuote.compareTo(secondQuote));
    }

    @Test
    public void compareTo_otherHasHigherPrice_returnsMinusOne() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=35.99|q=100|s=s");
        assertEquals(-1, firstQuote.compareTo(secondQuote));
    }

    @Test
    public void compareTo_otherHasLowerPrice_returnsOne() {
        Quote firstQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=35.99|q=100|s=s");
        Quote secondQuote = Quote.fromString("t=1638848595|i=BTCUSD|p=32.99|q=100|s=s");
        assertEquals(1, firstQuote.compareTo(secondQuote));
    }
}