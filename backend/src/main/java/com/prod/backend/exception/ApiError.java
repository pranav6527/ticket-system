package com.prod.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {
    private String message;
    private int status;
    private long timeStamp;
}
