package com.uet.psnbackend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Optional;

import com.uet.psnbackend.entity.AuthorizedEntity;
import com.uet.psnbackend.entity.DoubleIdObjectEntity;
import com.uet.psnbackend.entity.IdObjectEntity;
import com.uet.psnbackend.entity.UserEntity;
import com.uet.psnbackend.entity.UserSignInEntity;
import com.uet.psnbackend.repository.UserRepository;
import com.uet.psnbackend.service.JWTUtil;
import com.uet.psnbackend.service.ResponseObjectService;
import com.uet.psnbackend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/users")
    public ResponseEntity<ResponseObjectService> findAllUsers() {
        return new ResponseEntity<ResponseObjectService>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/users/profile")
    public ResponseEntity<ResponseObjectService> findById(@RequestBody IdObjectEntity inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findById(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/follow")
    public ResponseEntity<ResponseObjectService> followUser(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(userService.followUser(doubleId), HttpStatus.OK);
    }

    @PostMapping("/users/unfollow")
    public ResponseEntity<ResponseObjectService> unfollowUser(@RequestBody DoubleIdObjectEntity doubleId) {
        return new ResponseEntity<ResponseObjectService>(userService.unfollowUser(doubleId), HttpStatus.OK);
    }

    @PostMapping("/users/getfollowing")
    public ResponseEntity<ResponseObjectService> findFollowing(@RequestBody IdObjectEntity inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findFollowing(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/getfollower")
    public ResponseEntity<ResponseObjectService> findFollower(@RequestBody IdObjectEntity inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findFollower(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/save")
    public ResponseEntity<ResponseObjectService> saveUser(@RequestBody UserEntity inputUser) {
        return new ResponseEntity<ResponseObjectService>(userService.saveUser(inputUser), HttpStatus.OK);
    }

    @PostMapping("/users/signin")
    public ResponseEntity<ResponseObjectService> userSignIn(@RequestBody UserSignInEntity inputUser) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(inputUser.getEmail(), inputUser.getPassword()));
            String token = jwtUtil.generateToken(inputUser.getEmail());
            
            Optional<UserEntity> optUser = userRepo.findByEmail(inputUser.getEmail());
            UserEntity user = optUser.get();
            user.setPassword("");
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("success", "authenticated", new AuthorizedEntity(user, token)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("fail", "unauthenticated", null), HttpStatus.OK);
        }
    }

    @GetMapping("users/loginWithGoogle/{idToken}")
    public ResponseEntity<ResponseObjectService> loginWithGoogle(@PathVariable String idToken) throws GeneralSecurityException, IOException {
        UserEntity user = userService.loginWithGoogle(idToken);
        if(user != null) {
            String token = jwtUtil.generateToken(user.getEmail());
            user.setPassword("");
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("success", "authenticated", new AuthorizedEntity(user, token)), HttpStatus.OK);
        } else {
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("fail", "unauthenticated", null), HttpStatus.OK);
        }
    }

    @PutMapping("/users/update")
    public ResponseEntity<ResponseObjectService> update(@RequestBody UserEntity inputUser) {
        return new ResponseEntity<ResponseObjectService>(userService.update(inputUser), HttpStatus.OK);
    }

    @GetMapping ("/users/search/{username}")
    public ResponseEntity<ResponseObjectService> update(@PathVariable String username) {
        return new ResponseEntity<ResponseObjectService>(userService.searchUser(username), HttpStatus.OK);
    }

//    @GetMapping("/getdata")
//    public ResponseEntity<String> testAfterLogin(Principal p) {
//        return ResponseEntity.ok("Welcome. You are: " + p.getName());
//    }

    @GetMapping("/getdata")
    public Principal testAfterLogin(Principal p) {
        System.out.println(p.getName());
        return p;
    }

    @GetMapping()
    public String testAfterLogin() {
        return "Hello";
    }

}
