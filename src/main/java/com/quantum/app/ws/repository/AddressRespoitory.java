package com.quantum.app.ws.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.quantum.app.ws.io.entity.AddressEntity;
import com.quantum.app.ws.io.entity.UserEntity;

@Repository
public interface AddressRespoitory extends CrudRepository<AddressEntity, Long>{

	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

	AddressEntity findByAddressId(String addressId);

}
