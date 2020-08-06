package com.souf;

import com.souf.dto.IntervalDto;
import com.souf.dto.IntervalId;
import com.souf.flatten.RangeService;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlattenRangesTest {

    private static final RangeService rangeService = new RangeService();

    //for random generation
    private static int numberOfIntervals = 1000;
    private static BigInteger minLimit = new BigInteger("1000000000000");
    private static BigInteger maxLimit = new BigInteger("999999999999999999999");
    private static BigInteger rangeSpan = maxLimit.subtract(minLimit);
    private static SecureRandom rand = new SecureRandom();
    private static int maxLimitLength = maxLimit.bitLength();

    public static void main(String[] args) {

        List<IntervalDto> originalIntervals = new ArrayList<>();

        //create static list of ranges
        createStaticRanges(originalIntervals);

        //you can also create a random list of ranges
        //createRandomRanges(originalIntervals);

        System.out.println("Interval Set Creation Started: " + new Date());
        rangeService.generateRangeSetFromOriginalRanges(originalIntervals);
        System.out.println("Interval Set Creation Completed: " + new Date());

        System.out.println(rangeService.formatRangeSet());
        rangeService.printStatistics();

    }

    private static void createStaticRanges(List<IntervalDto> originalIntervals){
        originalIntervals.add(rangeService.generateRange("5", "10", 4));
        originalIntervals.add(rangeService.generateRange("15", "20", 1));
        originalIntervals.add(rangeService.generateRange("25", "30", 7));
        originalIntervals.add(rangeService.generateRange("31", "33", 2));
        originalIntervals.add(rangeService.generateRange("0", "35", 1));
        originalIntervals.add(rangeService.generateRange("35", "37", 6));
        originalIntervals.add(rangeService.generateRange("39", "50", 3));
        originalIntervals.add(rangeService.generateRange("20", "55", 1));
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

}
