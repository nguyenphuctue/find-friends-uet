package com.uet.psnbackend.repository;

import java.util.List;
import java.util.Optional;

import com.uet.psnbackend.entity.UserEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByUsername(String username);
    
}
