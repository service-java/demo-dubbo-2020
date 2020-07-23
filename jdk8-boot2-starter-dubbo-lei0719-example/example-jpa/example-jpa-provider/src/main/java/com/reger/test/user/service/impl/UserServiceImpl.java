package com.reger.test.user.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.reger.dubbo.annotation.Export;
import com.reger.test.user.dao.UserMapper;
import com.reger.test.user.model.User;
import com.reger.test.user.service.UserService;

@Export
public class UserServiceImpl implements UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional
	public void delAll() {
		userMapper.deleteAll();
	}

	@Override
	@Transactional 
	public User save(String name, String description) {
		String id=UUID.randomUUID().toString().replaceAll("-", "");
		User record=new User();
		record.setName(name);
		record.setId(id);
		record.setState(1);
		record.setDescription(description);
		log.info("{}",record);
		User user=userMapper.save(record);
		Assert.isTrue(user!=null, "插入数据失败");
		return user;
	}

	@Override
	public List<User> findAll() {
		return userMapper.findAll();
	}
}
