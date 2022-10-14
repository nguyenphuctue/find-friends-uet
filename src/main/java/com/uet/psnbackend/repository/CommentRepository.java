package com.uet.psnbackend.repository;

import com.uet.psnbackend.entity.CommentEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<CommentEntity, String> {
    
}