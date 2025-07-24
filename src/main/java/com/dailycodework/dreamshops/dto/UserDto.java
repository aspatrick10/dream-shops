package com.dailycodework.dreamshops.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private CartDto cart;
}
