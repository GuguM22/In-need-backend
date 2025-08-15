package com.In_need.inNeedApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String token;
    private String role;
    private String email;
    private boolean verified;

}
