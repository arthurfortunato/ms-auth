package com.ms.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private HttpStatus status;
    private String accessToken;
    private String message;
}
