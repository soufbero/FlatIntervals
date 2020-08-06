package com.souf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntervalDto {
    private IntervalId intervalId;
    private int value;
}
