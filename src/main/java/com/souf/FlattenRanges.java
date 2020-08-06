package com.souf;

import com.google.common.collect.*;
import com.souf.dto.IntervalDto;
import com.souf.dto.IntervalId;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FlattenRanges {

    private static RangeSet<BigInteger> intervalRangeSet;
    private static final BigInteger BIG_INTEGER_TOO_SMALL = new BigInteger("-1");
    private static final BigInteger BIG_INTEGER_TOO_LARGE = new BigInteger("1234567890123456789012");

    public static void main(String[] args) {

        List<IntervalDto> originalIntervals = new ArrayList<>();
        originalIntervals.add(generateRange("5", "10", 4));
        originalIntervals.add(generateRange("15", "20", 1));
        originalIntervals.add(generateRange("25", "30", 7));
        originalIntervals.add(generateRange("31", "33", 2));
        originalIntervals.add(generateRange("0", "35", 1));
        originalIntervals.add(generateRange("35", "37", 6));
        originalIntervals.add(generateRange("39", "50", 3));
        originalIntervals.add(generateRange("20", "55", 1));

        intervalRangeSet = TreeRangeSet.create();

        originalIntervals.forEach(intervalDto -> addRangeToSet(intervalDto));

    }

    private static void addRangeToSet(IntervalDto intervalDto) {
        Range<BigInteger> r = Range.closed(intervalDto.getIntervalId().getMin(), intervalDto.getIntervalId().getMax());
        System.out.println(r);
        RangeSet<BigInteger> overlaps = intervalRangeSet.subRangeSet(r);
        if (overlaps.isEmpty()) {
            intervalRangeSet.add(r);
        } else {
            RangeSet<BigInteger> overlapsComplement = overlaps.complement();
            RangeSet<BigInteger> complementCopy = TreeRangeSet.create(overlapsComplement);
            Range<BigInteger> minRange = complementCopy.rangeContaining(BIG_INTEGER_TOO_SMALL);
            Range<BigInteger> maxRange = complementCopy.rangeContaining(BIG_INTEGER_TOO_LARGE);
            complementCopy.remove(minRange);
            if (minRange.contains(r.lowerEndpoint())) {
                Range<BigInteger> minRangeReplacement =
                        Range.open(r.lowerEndpoint().subtract(BigInteger.ONE), minRange.upperEndpoint());
                complementCopy.add(minRangeReplacement);
            }
            complementCopy.remove(maxRange);
            if (maxRange.contains(r.upperEndpoint())) {
                Range<BigInteger> maxRangeReplacement =
                        Range.open(maxRange.lowerEndpoint(), r.upperEndpoint().add(BigInteger.ONE));
                complementCopy.add(maxRangeReplacement);
            }
            if (!complementCopy.isEmpty()) {
                complementCopy.asRanges().forEach(rng -> {
                    if (rng.upperEndpoint().subtract(rng.lowerEndpoint()).compareTo(BigInteger.ONE) > 0){
                        intervalRangeSet.add(Range.closed(
                                rng.lowerEndpoint().add(BigInteger.ONE), rng.upperEndpoint().subtract(BigInteger.ONE)));
                    }
                });
            }
        }
        System.out.println(intervalRangeSet);
    }

    private static IntervalDto generateRange(String min, String max, int value) {
        return IntervalDto.builder().intervalId(IntervalId.builder()
                .min(new BigInteger(min)).max(new BigInteger(max)).build()).value(value).build();
    }

}
