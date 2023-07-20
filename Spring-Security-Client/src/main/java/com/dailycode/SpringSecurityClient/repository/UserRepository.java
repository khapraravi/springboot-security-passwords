package com.dailycode.SpringSecurityClient.repository;

import com.dailycode.SpringSecurityClient.entity.User;
import com.dailycode.SpringSecurityClient.model.PasswordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
}
