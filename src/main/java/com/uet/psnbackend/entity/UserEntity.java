package com.uet.psnbackend.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "User")
public class UserEntity {
    @Id
    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String role;

    List<String> following = new ArrayList<>();

    List<String> follower = new ArrayList<>();

    private String image;

    public UserEntity(String id, String username,String firstName,String lastName,String email,String role,String image){
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.image = image;
    }
}
