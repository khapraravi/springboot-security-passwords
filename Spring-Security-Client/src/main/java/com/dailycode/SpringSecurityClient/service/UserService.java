package com.dailycode.SpringSecurityClient.service;

import com.dailycode.SpringSecurityClient.entity.User;
import com.dailycode.SpringSecurityClient.entity.VerificationToken;
import com.dailycode.SpringSecurityClient.model.PasswordModel;
import com.dailycode.SpringSecurityClient.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String validateUser(String token);

    VerificationToken generateNewToken(String oldtoken);


    User findUserByEmail(PasswordModel passwordModel);


    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkOldPassword(User user, PasswordModel passwordModel);
}
