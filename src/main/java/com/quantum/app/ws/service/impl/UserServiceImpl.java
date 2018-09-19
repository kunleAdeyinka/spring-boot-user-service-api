package com.quantum.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.quantum.app.ws.exceptions.UserServiceException;
import com.quantum.app.ws.io.entity.UserEntity;
import com.quantum.app.ws.repository.UserRepository;
import com.quantum.app.ws.service.UserService;
import com.quantum.app.ws.shared.Utils;
import com.quantum.app.ws.shared.dto.AddressDTO;
import com.quantum.app.ws.shared.dto.UserDto;
import com.quantum.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;


	@Override
	public UserDto createUser(UserDto user) {
		
		UserEntity savedUserEntity = userRepository.findByEmail(user.getEmail());
		
		if(savedUserEntity != null) throw new RuntimeException("User already esists");
		
		for(int i=0; i < user.getAddresses().size(); i++) {
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}
		
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationStatus(false);
		
		UserEntity storedUserEntity = userRepository.save(userEntity);		
		UserDto returnUser = modelMapper.map(storedUserEntity, UserDto.class);
		
		return returnUser;
	}


	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}


	@Override
	public UserDto getUser(String email) {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userEntity, userDto);
		
		return userDto;
	}


	@Override
	public UserDto getUserByUserId(String id) {
		
		UserDto userDto = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(id);
		
		if(userEntity == null) throw new UsernameNotFoundException(id);
		
		BeanUtils.copyProperties(userEntity, userDto);
		
		return userDto;
	}


	@Override
	public UserDto updateUser(String userId, UserDto user) {
		
		UserDto userDto = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updatedUserEntity = userRepository.save(userEntity);
		
		BeanUtils.copyProperties(updatedUserEntity, userDto);
		return userDto;
	}


	@Override
	public void deleteUser(String userId) {
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userEntity);
		
	}


	@Override
	public List<UserDto> getUsers(int page, int limit) {
		
		List<UserDto> result = new ArrayList<>();
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
		for(UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			result.add(userDto);
		}
		
		return result;
	}

}
