package com.dailycodework.dreamshops.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String username;
    private List<String> roles;
    private String accessToken;
}
