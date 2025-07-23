package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.constant.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private Role role;
    private String password;
    private String confirmPassword;

}