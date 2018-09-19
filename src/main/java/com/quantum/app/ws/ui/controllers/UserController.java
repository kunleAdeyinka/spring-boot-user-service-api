package com.quantum.app.ws.ui.controllers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quantum.app.ws.service.AddressService;
import com.quantum.app.ws.service.UserService;
import com.quantum.app.ws.shared.dto.AddressDTO;
import com.quantum.app.ws.shared.dto.UserDto;
import com.quantum.app.ws.ui.model.request.UserDetailsRequestModel;
import com.quantum.app.ws.ui.model.response.AddressesRest;
import com.quantum.app.ws.ui.model.response.OperationStatusModel;
import com.quantum.app.ws.ui.model.response.RequestOperationStatus;
import com.quantum.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;

	@GetMapping(path="/{id}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {
		
		UserRest returnValue = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		
		return returnValue;
	}
	
	@PostMapping(consumes={ MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces={ MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
	
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		UserRest returnValue = modelMapper.map(createdUser, UserRest.class);		
		
		return returnValue;
	}
	
	@PutMapping(path="/{id}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		
		UserRest returnValue = new UserRest();
		UserDto userDto = new UserDto();		
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		
		
		return returnValue;
	}
	
	@DeleteMapping(path="/{id}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel opStatusModel = new OperationStatusModel();
		opStatusModel.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		opStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return opStatusModel;
	}
	
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,@RequestParam(value = "limit", defaultValue = "2") int limit) {
		
		List<UserRest> results = new ArrayList<>();
		if(page > 0) {
			page -=1;
		}
		List<UserDto> users = userService.getUsers(page, limit);
		
		for(UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			results.add(userModel);
		}
		
		return results;
	}
	
	@GetMapping(path="/{id}/addresses", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resources<AddressesRest> getUserAddresses(@PathVariable String id) {
		
		List<AddressesRest> addressesList = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		List<AddressDTO> addressDtos = addressService.getAddresses(id);
		
		if(addressDtos != null && !addressDtos.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();		
			addressesList = modelMapper.map(addressDtos, listType);
			
			//create hateaos links
			for(AddressesRest addressRest : addressesList) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				Link userLink = linkTo(UserController.class).slash(id).withRel("user");
				
				addressRest.add(addressLink);
				addressRest.add(userLink);
			}
		}	
		
		
		return new Resources<>(addressesList);
	}
	
	@GetMapping(path="/{userId}/addresses/{addressId}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		
		AddressDTO addressDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();	
		
		//hateos links
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		AddressesRest addressesRestModel = modelMapper.map(addressDto, AddressesRest.class);
		addressesRestModel.add(addressLink);
		addressesRestModel.add(userLink);
		addressesRestModel.add(addressesLink);
		
		return new Resource<>(addressesRestModel);
	}
}
