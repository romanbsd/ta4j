/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Ta4j Organization & respective
 * authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ta4j.core.criteria.pnl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ta4j.core.TestUtils.assertNumEquals;

import java.util.function.Function;

import org.junit.Test;
import org.ta4j.core.AnalysisCriterion;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.criteria.AbstractCriterionTest;
import org.ta4j.core.mocks.MockBarSeries;
import org.ta4j.core.num.Num;

public class ProfitLossCriterionTest extends AbstractCriterionTest {

    public ProfitLossCriterionTest(Function<Number, Num> numFunction) {
        super((params) -> new ProfitLossCriterion(), numFunction);
    }

    @Test
    public void calculateOnlyWithProfitPositions() {
        MockBarSeries series = new MockBarSeries(numFunction, 100, 105, 110, 100, 95, 105);
        TradingRecord tradingRecord = new BaseTradingRecord(Trade.buyAt(0, series, series.numOf(50)),
                Trade.sellAt(2, series, series.numOf(50)), Trade.buyAt(3, series, series.numOf(50)),
                Trade.sellAt(5, series, series.numOf(50)));

        AnalysisCriterion profit = getCriterion();
        assertNumEquals(500 + 250, profit.calculate(series, tradingRecord));
    }

    @Test
    public void calculateOnlyWithLossPositions() {
        MockBarSeries series = new MockBarSeries(numFunction, 100, 95, 100, 80, 85, 70);
        TradingRecord tradingRecord = new BaseTradingRecord(Trade.buyAt(0, series, series.numOf(50)),
                Trade.sellAt(1, series, series.numOf(50)), Trade.buyAt(2, series, series.numOf(50)),
                Trade.sellAt(5, series, series.numOf(50)));

        AnalysisCriterion profit = getCriterion();
        assertNumEquals(-250 - 1500, profit.calculate(series, tradingRecord));
    }

    @Test
    public void calculateOnlyWithProfitShortPositions() {
        MockBarSeries series = new MockBarSeries(numFunction, 100, 105, 110, 100, 95, 105);
        TradingRecord tradingRecord = new BaseTradingRecord(Trade.sellAt(0, series, series.numOf(50)),
                Trade.buyAt(2, series, series.numOf(50)), Trade.sellAt(3, series, series.numOf(50)),
                Trade.buyAt(5, series, series.numOf(50)));

        AnalysisCriterion profit = getCriterion();
        assertNumEquals(-(500 + 250), profit.calculate(series, tradingRecord));
    }

    @Test
    public void calculateOnlyWithLossShortPositions() {
        MockBarSeries series = new MockBarSeries(numFunction, 100, 95, 100, 80, 85, 70);
        TradingRecord tradingRecord = new BaseTradingRecord(Trade.sellAt(0, series, series.numOf(50)),
                Trade.buyAt(1, series, series.numOf(50)), Trade.sellAt(2, series, series.numOf(50)),
                Trade.buyAt(5, series, series.numOf(50)));

        AnalysisCriterion profit = getCriterion();
        assertNumEquals(250 + 1500, profit.calculate(series, tradingRecord));
    }

    @Test
    public void betterThan() {
        AnalysisCriterion criterion = getCriterion();
        assertTrue(criterion.betterThan(numOf(5000), numOf(4500)));
        assertFalse(criterion.betterThan(numOf(4500), numOf(5000)));
    }

    @Test
    public void testCalculateOneOpenPositionShouldReturnZero() {
        openedPositionUtils.testCalculateOneOpenPositionShouldReturnExpectedValue(numFunction, getCriterion(), 0);
    }

}
