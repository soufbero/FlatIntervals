package com.souf.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class IntervalId {
    private BigInteger min;
    private BigInteger max;
}
