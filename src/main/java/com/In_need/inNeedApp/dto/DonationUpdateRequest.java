package com.In_need.inNeedApp.dto;

import com.In_need.inNeedApp.constant.DonationStatus;
import lombok.Data;

@Data
public class DonationUpdateRequest {
    private Long id;
    private DonationStatus status;
}

