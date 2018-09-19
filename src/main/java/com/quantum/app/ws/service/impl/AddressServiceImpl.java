package com.quantum.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quantum.app.ws.io.entity.AddressEntity;
import com.quantum.app.ws.io.entity.UserEntity;
import com.quantum.app.ws.repository.AddressRespoitory;
import com.quantum.app.ws.repository.UserRepository;
import com.quantum.app.ws.service.AddressService;
import com.quantum.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRespoitory addressRepository;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		
		List<AddressDTO> results = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) {
			return results;
		}
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		
		for(AddressEntity addressEntity : addresses) {
			results.add(modelMapper.map(addressEntity, AddressDTO.class));
		}
		
		return results;
	}

	@Override
	public AddressDTO getAddress(String addressId) {
		
		AddressDTO result = null;
		
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		
		if(addressEntity != null) {
			result = new ModelMapper().map(addressEntity, AddressDTO.class);
		}
		
		return result;
	}

}
