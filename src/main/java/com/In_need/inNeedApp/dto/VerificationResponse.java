package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {
        private Long id;
        private String phone;
        private String website;
        private List<String> documents;
        private Status status;
        private String email; // add this
        private Long userId;

}
