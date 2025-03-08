package com.carrefour.deliveryapp.exceptions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
@Builder
public class ErrorBody {
    private Integer code;
    private String message;
}
