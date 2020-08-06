package com.souf;

import com.google.common.collect.*;
import com.souf.dto.IntervalDto;
import com.souf.dto.IntervalId;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlattenRanges {

    private static RangeSet<BigInteger> intervalRangeSet;
    private static final BigInteger BIG_INTEGER_TOO_SMALL = new BigInteger("-1");
    private static final BigInteger BIG_INTEGER_TOO_LARGE = new BigInteger("1234567890123456789012");

    //for random generation
    private static int numberOfIntervals = 10000000;
    private static BigInteger minLimit = new BigInteger("1000000000000");
    private static BigInteger maxLimit = new BigInteger("999999999999999999999");
    private static BigInteger rangeSpan = maxLimit.subtract(minLimit);
    private static SecureRandom rand = new SecureRandom();
    private static int maxLimitLength = maxLimit.bitLength();

    //statistics
    private static int numOfNonOverlaps = 0;
    private static int numOfOverlaps = 0;
    private static int numOfRangesSplit = 0;
    private static int numOfRangesSkipped = 0;


    public static void main(String[] args) {

        List<IntervalDto> originalIntervals = new ArrayList<>();

        //create static list of ranges
        //createStaticRanges(originalIntervals);

        //you can also create a random list of ranges
        createRandomRanges(originalIntervals);

        intervalRangeSet = TreeRangeSet.create();

        System.out.println("Interval Set Creation Started: " + new Date());
        originalIntervals.forEach(intervalDto -> addRangeToSet(intervalDto));
        System.out.println("Interval Set Creation Completed: " + new Date());

        printStatistics();

    }

    private static void addRangeToSet(IntervalDto intervalDto) {
        Range<BigInteger> r = Range.closed(intervalDto.getIntervalId().getMin(), intervalDto.getIntervalId().getMax());
        //System.out.println(r);
        RangeSet<BigInteger> overlaps = intervalRangeSet.subRangeSet(r);
        if (overlaps.isEmpty()) {
            numOfNonOverlaps++;
            intervalRangeSet.add(r);
        } else {
            numOfOverlaps++;
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
                numOfRangesSplit++;
                complementCopy.asRanges().forEach(rng -> {
                    if (rng.upperEndpoint().subtract(rng.lowerEndpoint()).compareTo(BigInteger.ONE) > 0){
                        intervalRangeSet.add(Range.closed(
                                rng.lowerEndpoint().add(BigInteger.ONE), rng.upperEndpoint().subtract(BigInteger.ONE)));
                    }
                });
            }else{
                numOfRangesSkipped++;
            }
        }
        //System.out.println(intervalRangeSet);
    }

    private static void createStaticRanges(List<IntervalDto> originalIntervals){
        originalIntervals.add(generateRange("5", "10", 4));
        originalIntervals.add(generateRange("15", "20", 1));
        originalIntervals.add(generateRange("25", "30", 7));
        originalIntervals.add(generateRange("31", "33", 2));
        originalIntervals.add(generateRange("0", "35", 1));
        originalIntervals.add(generateRange("35", "37", 6));
        originalIntervals.add(generateRange("39", "50", 3));
        originalIntervals.add(generateRange("20", "55", 1));
    }

    private static void createRandomRanges(List<IntervalDto> originalIntervals){
        System.out.println("Number of Random Ranges: " + numberOfIntervals);
        System.out.println("Random Ranges Creation Started: " + new Date());
        for (int i = 0; i < numberOfIntervals; i++){
            BigInteger edge1 = getRandomBigInteger();
            BigInteger edge2 = getRandomBigInteger();
            int value = rand.nextInt(Integer.MAX_VALUE);
            originalIntervals.add(IntervalDto.builder()
                    .intervalId(IntervalId.builder()
                            .min(edge1.compareTo(edge2) > 0 ? edge2 : edge1)
                            .max(edge1.compareTo(edge2) > 0 ? edge1 : edge2)
                            .build())
                    .value(value)
                    .build());
        }
        System.out.println("Random Ranges Creation Completed: " + new Date());
    }

    private static BigInteger getRandomBigInteger(){
        BigInteger res = new BigInteger(maxLimitLength, rand);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(rangeSpan) >= 0)
            res = res.mod(rangeSpan).add(minLimit);
        return res;
    }

    private static IntervalDto generateRange(String min, String max, int value) {
        return IntervalDto.builder().intervalId(IntervalId.builder()
                .min(new BigInteger(min)).max(new BigInteger(max)).build()).value(value).build();
    }

    private static void printStatistics() {
        System.out.println("Number of intervals NOT overlapping with other intervals during insertion: "
                + numOfNonOverlaps);
        System.out.println("Number of intervals overlapping with other intervals during insertion: "
                + numOfOverlaps);
        System.out.println("Number of intervals that were split into smaller ranges then inserted: "
                + numOfRangesSplit);
        System.out.println("Number of intervals that were skipped because they entirely overlapped with other ranges: "
                + numOfRangesSkipped);
    }

}
