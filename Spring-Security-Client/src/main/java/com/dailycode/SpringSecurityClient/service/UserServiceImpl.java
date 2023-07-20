package com.dailycode.SpringSecurityClient.service;

import com.dailycode.SpringSecurityClient.entity.PasswordResetToken;
import com.dailycode.SpringSecurityClient.entity.User;
import com.dailycode.SpringSecurityClient.entity.VerificationToken;
import com.dailycode.SpringSecurityClient.model.PasswordModel;
import com.dailycode.SpringSecurityClient.model.UserModel;
import com.dailycode.SpringSecurityClient.repository.PasswordResetRepository;
import com.dailycode.SpringSecurityClient.repository.UserRepository;
import com.dailycode.SpringSecurityClient.repository.VerificationTokenRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setRole("USER");
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRepository.save(user);

        return  user;

    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user,token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            return "invalid";
        }
        User user = verificationToken.getUser();
        Calendar calender = Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime() - calender.getTime().getTime() <= 0){
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
       user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewToken(String oldtoken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldtoken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public User findUserByEmail(PasswordModel passwordModel) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        return user;
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetRepository.findByToken(token);
        if(passwordResetToken == null){
            return "invalid";
        }
        User user = passwordResetToken.getUser();
        Calendar calender = Calendar.getInstance();

        if(passwordResetToken.getExpirationTime().getTime() - calender.getTime().getTime() <= 0){
            passwordResetRepository.delete(passwordResetToken);
            return "expired";
        }
        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {

        return Optional.ofNullable(passwordResetRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkOldPassword(User user, PasswordModel passwordModel) {
        return passwordEncoder.matches(passwordModel.getOldPassword(),user.getPassword());
    }


}
