package com.crm.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LeadDto {
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Company cannot be empty")
    private String company;

    private String phone;

    @NotEmpty(message = "Status cannot be empty")
    private String status;
}