package com.souf.flatten;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.souf.dto.IntervalDto;
import com.souf.dto.IntervalId;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RangeService {

    private static final SecureRandom rand = new SecureRandom();

    private static RangeSet<BigInteger> intervalRangeSet;
    private static final BigInteger BIG_INTEGER_TOO_SMALL = new BigInteger("-1");
    private static final BigInteger BIG_INTEGER_TOO_LARGE = new BigInteger("1234567890123456789012");

    //statistics
    private static int numOfNonOverlaps = 0;
    private static int numOfOverlaps = 0;
    private static int numOfRangesSplit = 0;
    private static int numOfRangesSkipped = 0;

    private static List<String> outputResults;

    public List<IntervalDto> generateRangesFromString(String input){
        List<IntervalDto> intervals = new ArrayList<>();
        List<String> lines = Arrays.asList(input.split("\\r?\\n"));
        for (String line: lines){
            if (line.contains(",")){
                IntervalDto intervalDto = generateRange(line.split(",")[0],
                        line.split(",")[1],rand.nextInt(1000));
                intervals.add(intervalDto);
            }
        }
        return intervals;
    }

    public IntervalDto generateRange(String min, String max, int value) {
        return IntervalDto.builder().intervalId(IntervalId.builder()
                .min(new BigInteger(min)).max(new BigInteger(max)).build()).value(value).build();
    }

    public RangeSet<BigInteger> getRangeSetFromOriginalRanges(List<IntervalDto> originalRanges){
        reInitializeValues();
        originalRanges.forEach(intervalDto -> addRangeToSet(intervalDto));
        return intervalRangeSet;
    }

    private void reInitializeValues(){
        intervalRangeSet = TreeRangeSet.create();
        numOfNonOverlaps = 0;
        numOfOverlaps = 0;
        numOfRangesSplit = 0;
        numOfRangesSkipped = 0;
        outputResults = new ArrayList<>();
    }

    private static void addRangeToSet(IntervalDto intervalDto) {
        outputResults.add("------------------------------------------");
        Range<BigInteger> r = Range.closed(intervalDto.getIntervalId().getMin(), intervalDto.getIntervalId().getMax());
        outputResults.add(r.toString());
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
                    if (rng.upperEndpoint().subtract(rng.lowerEndpoint()).compareTo(BigInteger.ONE) > 0) {
                        intervalRangeSet.add(Range.closed(
                                rng.lowerEndpoint().add(BigInteger.ONE), rng.upperEndpoint().subtract(BigInteger.ONE)));
                    }
                });
            } else {
                numOfRangesSkipped++;
            }
        }
        outputResults.add(intervalRangeSet.toString());
    }

    public String formatRangeSet(){
        return String.join("\n",outputResults);
    }

    public void printStatistics() {
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
