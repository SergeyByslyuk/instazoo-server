package com.sergeytechnologies.instazoo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTokenSuccessResponse {

    private boolean success;
    private String token;
}
