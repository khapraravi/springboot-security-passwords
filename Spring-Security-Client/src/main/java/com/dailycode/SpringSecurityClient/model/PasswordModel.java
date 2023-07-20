package com.dailycode.SpringSecurityClient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordModel {
    private String email;
    private String newPassword;
    private String oldPassword;
}
