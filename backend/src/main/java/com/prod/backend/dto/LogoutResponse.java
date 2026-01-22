package com.prod.backend.dto;

import lombok.Data;

@Data
public class LogoutResponse {
    private String message;

    public LogoutResponse(String message) {
        this.message = message;
    }
}
