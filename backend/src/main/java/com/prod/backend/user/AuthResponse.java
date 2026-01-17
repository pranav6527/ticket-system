package com.prod.backend.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponse {

    private String accessToken;
}
