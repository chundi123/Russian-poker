package com.demo.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String username;
    private String status;
    private Instant createdAt;
    private Instant lastUpdated;
    private Long platformId;
    private String platformCode;
    private String platformName;
}
