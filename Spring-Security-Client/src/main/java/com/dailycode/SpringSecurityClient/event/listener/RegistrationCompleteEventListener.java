package com.dailycode.SpringSecurityClient.event.listener;

import com.dailycode.SpringSecurityClient.entity.User;
import com.dailycode.SpringSecurityClient.event.RegistrationCompleteEvent;
import com.dailycode.SpringSecurityClient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent>{

    @Autowired
  private UserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //create verrification token

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user,token);

        //send mail for verification

        String url = event.getApplicationUrl() +
                "/verifyregistration?token=" + token;

        //send verification link mail
        log.info("Click link to verify account : {}" + url);
    }
}
