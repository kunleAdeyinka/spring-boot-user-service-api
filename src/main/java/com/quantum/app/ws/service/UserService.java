package com.quantum.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.quantum.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService{
	
	public UserDto createUser(UserDto user);
	public UserDto getUser(String email);
	public UserDto getUserByUserId(String id);
	public UserDto updateUser(String userId, UserDto userDto);
	public void deleteUser(String userId);
	public List<UserDto> getUsers(int page, int limit);

}
