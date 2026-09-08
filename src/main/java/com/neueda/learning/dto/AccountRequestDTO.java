package com.neueda.learning.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountRequestDTO(

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    String name,
//    @NotNull(message = "ID is required")
//    int id,
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Balance cannot be negative")
    BigDecimal balance
     ){

}