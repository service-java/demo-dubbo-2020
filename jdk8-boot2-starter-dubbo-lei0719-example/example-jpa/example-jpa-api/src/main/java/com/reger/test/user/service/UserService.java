package com.reger.test.user.service;


import java.util.List;

import com.reger.test.user.model.User;

public interface UserService {
	List<User> findAll();
	
	void delAll();
	
	User save(String name, String description);

}
