package com.uet.psnbackend.repository;

import com.uet.psnbackend.entity.ImageDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends MongoRepository<ImageDataEntity, String> {

    Optional<ImageDataEntity> findByName(String fileName);

    Optional<ImageDataEntity> findById(String idImage);
}
