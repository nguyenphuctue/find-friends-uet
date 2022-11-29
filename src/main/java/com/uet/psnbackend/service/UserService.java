package com.uet.psnbackend.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.uet.psnbackend.entity.DoubleIdObjectEntity;
import com.uet.psnbackend.entity.IdObjectEntity;
import com.uet.psnbackend.entity.UserEntity;
import com.uet.psnbackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    public ResponseObjectService findAll() {
        ResponseObjectService responseObj = new ResponseObjectService();
        responseObj.setPayload(userRepo.findAll());
        responseObj.setStatus("success");
        responseObj.setMessage("success");
        return responseObj;
    }

    public ResponseObjectService findById(String id) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optUser = userRepo.findById(id);
        if (optUser.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("user id: " + id + " not existed");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            responseObj.setPayload(optUser.get());
            responseObj.setStatus("success");
            responseObj.setMessage("success");
            return responseObj;
        }
    }

    public ResponseObjectService findFollowing(String id) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optUser = userRepo.findById(id);
        if (optUser.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("user id: " + id + " not existed");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            List<String> followingIds = optUser.get().getFollowing();
            List<UserEntity> followingAccounts = new ArrayList<>();
            if (followingIds.size() > 0) {
                for (String followingId : followingIds) {
                    Optional<UserEntity> optFollowingUser = userRepo.findById(followingId);
                    if (optFollowingUser.isPresent()) {
                        UserEntity followingUser = optFollowingUser.get();
                        followingUser.setPassword("");
                        followingAccounts.add(followingUser);
                    } 
                }
                responseObj.setStatus("success");
                responseObj.setMessage("success");
                responseObj.setPayload(followingAccounts);
                return responseObj;
            } else {
                responseObj.setStatus("fail");
                responseObj.setMessage("User id " + id + " does not follow anyone");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService findFollower(String id) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optUser = userRepo.findById(id);
        if (optUser.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("user id: " + id + " not existed");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            List<String> followerIds = optUser.get().getFollower();
            List<UserEntity> followerAccounts = new ArrayList<>();
            if (followerIds.size() > 0) {
                for (String followerId : followerIds) {
                    Optional<UserEntity> optFollowerUser = userRepo.findById(followerId);
                    if (optFollowerUser.isPresent()) {
                        UserEntity followerUser = optFollowerUser.get();
                        followerUser.setPassword("");
                        followerAccounts.add(followerUser);
                    } 
                }
                responseObj.setStatus("success");
                responseObj.setMessage("success");
                responseObj.setPayload(followerAccounts);
                return responseObj;
            } else {
                responseObj.setStatus("fail");
                responseObj.setMessage("User id " + id + " does not have any follower");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService saveUser(UserEntity inputUser) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optUser = userRepo.findByEmail(inputUser.getEmail());
        if (optUser.isPresent()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("Email address " + inputUser.getEmail() + " existed");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            inputUser.setPassword(bCryptEncoder.encode(inputUser.getPassword()));
            
            // user follows himself so he could get his posts in newsfeed as well
            UserEntity user = userRepo.save(inputUser);
            List<String> listFollowing = user.getFollowing();
            if (listFollowing == null) {
                listFollowing = new ArrayList<>();
            }
            listFollowing.add(user.getId());
            user.setFollowing(listFollowing);
            this.updateWithoutPassword(user);
            responseObj.setPayload(user);
            responseObj.setStatus("success");
            responseObj.setMessage("success");
            return responseObj;
        }
    }

    public boolean updateWithoutPassword(UserEntity inputUser) {
        Optional<UserEntity> optUser = userRepo.findById(inputUser.getId());
        if (optUser.isEmpty()) {
            return false;
        } else {
            UserEntity currentUser = optUser.get();
            if (inputUser.getPassword().equals(currentUser.getPassword())) {
                userRepo.save(inputUser);
                return true;
            } else {
                return false;
            }
        }
    }

    public ResponseObjectService update(UserEntity inputUser) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optUser = userRepo.findById(inputUser.getId());
        if (optUser.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("user id: " + inputUser.getId() + " not existed");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserEntity currentUser = optUser.get();
            if (bCryptEncoder.matches(inputUser.getPassword(), currentUser.getPassword())) {
                inputUser.setPassword(bCryptEncoder.encode(inputUser.getPassword()));
                responseObj.setPayload(userRepo.save(inputUser));
                responseObj.setStatus("success");
                responseObj.setMessage("success");
                return responseObj;
            } else {
                responseObj.setStatus("fail");
                responseObj.setMessage("current password is not correct");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService searchUser(String username) {
        List<UserEntity> users = userRepo.findByUsername(username);
        ResponseObjectService responseObj = new ResponseObjectService();
        responseObj.setStatus("success");
        responseObj.setMessage("Search user successful");
        responseObj.setPayload(users);
        return  responseObj;
    }

    public ResponseObjectService followUser(DoubleIdObjectEntity doubleId) {
        // id1 - followed user, id2 - follower

        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optFollowedUser = userRepo.findById(doubleId.getId1());
        Optional<UserEntity> optFollower = userRepo.findById(doubleId.getId2());
        if (optFollowedUser.isEmpty() || optFollower.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("invalid user id");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserEntity followedUser = optFollowedUser.get();
            UserEntity follower = optFollower.get();

            // add to follower list
            List<String> followerList = followedUser.getFollower();
            if (followerList == null) {
                followerList = new ArrayList<>();
            }
            followerList.add(follower.getId());
            followedUser.setFollower(followerList);

            // add to following list
            List<String> followingList = follower.getFollowing();
            if (followingList == null) {
                followingList = new ArrayList<>();
            }
            followingList.add(followedUser.getId());
            follower.setFollowing(followingList);

            userRepo.save(followedUser);
            userRepo.save(follower);

            responseObj.setStatus("success");
            responseObj.setMessage(
                    "User id " + follower.getId() + " successfully followed user id " + followedUser.getId());
            responseObj.setPayload(new IdObjectEntity(doubleId.getId1()));
            return responseObj;
        }
    }

    public ResponseObjectService unfollowUser(DoubleIdObjectEntity doubleId) {
        // id1 - followed user, id2 - follower

        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserEntity> optFollowedUser = userRepo.findById(doubleId.getId1());
        Optional<UserEntity> optFollower = userRepo.findById(doubleId.getId2());
        if (optFollowedUser.isEmpty() || optFollower.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("invalid user id");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserEntity followedUser = optFollowedUser.get();
            UserEntity follower = optFollower.get();

            // add to follower list
            List<String> followerList = followedUser.getFollower();
            if (followerList == null) {
                followerList = new ArrayList<>();
            }
            followerList.remove(follower.getId());
            followedUser.setFollower(followerList);

            // add to following list
            List<String> followingList = follower.getFollowing();
            if (followingList == null) {
                followingList = new ArrayList<>();
            }
            followingList.remove(followedUser.getId());
            follower.setFollowing(followingList);

            userRepo.save(followedUser);
            userRepo.save(follower);

            responseObj.setStatus("success");
            responseObj.setMessage(
                    "User id " + follower.getId() + " successfully unfollowed user id " + followedUser.getId());
            responseObj.setPayload(new IdObjectEntity(doubleId.getId1()));
            return responseObj;
        }
    }

    // important for security
    // Use user email as unique field to login instead of username
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> optUser = userRepo.findByEmail(email);
        User springUser = null;

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException("Cannot find user with email: " + email);
        } else {
            UserEntity foundUser = optUser.get();
            String role = foundUser.getRole();
            Set<GrantedAuthority> ga = new HashSet<>();
            ga.add(new SimpleGrantedAuthority(role));
            springUser = new User(foundUser.getEmail(), foundUser.getPassword(), ga);
            return springUser;
        }
    }

    //Login with google
    public UserEntity loginWithGoogle(String idTokenString) throws GeneralSecurityException, IOException {
        ResponseObjectService responseObj = new ResponseObjectService();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier( new NetHttpTransport(), new JacksonFactory());

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            Optional<UserEntity> optUser = userRepo.findByEmail(email);
            if(optUser.isPresent()){
                UserEntity user = optUser.get();
                return user;
            } else {
                UserEntity user = new UserEntity(userId, "user"+userId, givenName, familyName, email, "user", pictureUrl);
                return user;
            }
        } else {
            return null;
        }
    }
}
