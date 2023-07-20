package com.dailycode.SpringSecurityClient.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor

public class PasswordResetToken {


    private static final int EXPIRATION_TIME = 10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_TOKEN"))
    private User user;

    public PasswordResetToken(User user,String token){
        this.user = user;
        this.token = token;
        this.expirationTime = calculateExpirateDate(EXPIRATION_TIME);
    }

    public  PasswordResetToken(String token){
        super();
        this.token = token;
        this.expirationTime = calculateExpirateDate(EXPIRATION_TIME);
    }

    private Date calculateExpirateDate(int expirationTime) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(new Date().getTime());
        calender.add(Calendar.MINUTE,expirationTime);
        return new Date(calender.getTime().getTime());
    }



}
