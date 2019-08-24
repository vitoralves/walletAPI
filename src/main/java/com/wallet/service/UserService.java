package com.wallet.service;

import java.util.Optional;

import com.wallet.entity.User;

public interface UserService {

	User save(User u);
	
	Optional<User> findByEmail(String email);
}
