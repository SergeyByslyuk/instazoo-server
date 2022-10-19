package com.sergeytechnologies.instazoo.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {

    @NotEmpty(message = "Username canny be empty")
    private String username;
    @NotEmpty(message = "Password canny be empty")
    private String password;





}
