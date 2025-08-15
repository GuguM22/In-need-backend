package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.constant.Role;

public record UserInfoDTO(Long id, String email, String username, Role role, boolean verified) {
}
