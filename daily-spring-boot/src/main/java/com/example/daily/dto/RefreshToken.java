package com.example.daily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RefreshToken {
    private String username;
    private String refreshToken;
}
