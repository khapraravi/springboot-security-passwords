package com.dailycode.SpringSecurityClient.controller;

import com.dailycode.SpringSecurityClient.entity.PasswordResetToken;
import com.dailycode.SpringSecurityClient.entity.User;
import com.dailycode.SpringSecurityClient.entity.VerificationToken;
import com.dailycode.SpringSecurityClient.event.RegistrationCompleteEvent;
import com.dailycode.SpringSecurityClient.model.PasswordModel;
import com.dailycode.SpringSecurityClient.model.UserModel;
import com.dailycode.SpringSecurityClient.repository.PasswordResetRepository;
import com.dailycode.SpringSecurityClient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping("/welcome")
    public String welcomePage() {
        return "Welcome to SpringBoot Demo Application!!";
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyregistration")
    public String verifyuser(@RequestParam("token") String token) {
        String result = userService.validateUser(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User is validated with token";
        } else {
            return "token is expired";
        }
    }

    @PostMapping("/resetpassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel);
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
            passwordResetRepository.save(passwordResetToken);
            url = sendPasswordResetLink(user, applicationUrl(request), token);
            return url;
        }else {
            return "user not found";
        }
    }

    @PostMapping("/changepassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user = userService.findUserByEmail(passwordModel);
        if(user != null){
            if(!userService.checkOldPassword(user,passwordModel)){
                return "invalid old password";
            }
            else{
                userService.changePassword(user,passwordModel.getNewPassword());
                return "password reset successfully!";
            }
        }else {
            return "Bad credentails, email not found";
        }

    }

    @PostMapping("/savepassword")
    public String savePassword(@RequestParam("token") String token,@RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
                return "invalid user";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(),passwordModel.getNewPassword());

            return "password reset successfully";

        }
        else{
            return "user not found for specified token";
        }




    }

    private String sendPasswordResetLink(User user,String applicationUrl,String token) {
        String url = applicationUrl+
                "/savepassword?token=" + token;
        //send verification link mail
        log.info("Click link to reset your password: {}" + url);

        return url;

    }

    @GetMapping("/resendverificationtoken")
    public String resendToken(@RequestParam("token") String oldtoken,HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewToken(oldtoken);
        User user = verificationToken.getUser();
        resendverificationToken(verificationToken,applicationUrl(request));
        return "verificcation link send again";
    }

    private void resendverificationToken(VerificationToken verificationToken, String applicationUrl) {
        String url = applicationUrl+
                "/verifyregistration?token=" + verificationToken.getToken();
        //send verification link mail
        log.info("Click link to verify account : {}" + url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"+ request.getServerName() + ":"+request.getServerPort()+request.getContextPath();
    }
}
