package com.reger.test.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reger.dubbo.annotation.Export;
import com.reger.test.user.model.User;

@Export
public interface UserMapper extends  JpaRepository<User, String> {
	 User findByName(String name);
}