package com.sergeytechnologies.instazoo.service;

import com.sergeytechnologies.instazoo.dto.UserDTO;
import com.sergeytechnologies.instazoo.entity.User;
import com.sergeytechnologies.instazoo.entity.enums.ERole;
import com.sergeytechnologies.instazoo.exceptions.UserExistException;
import com.sergeytechnologies.instazoo.payload.request.SignupRequest;
import com.sergeytechnologies.instazoo.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {

    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder cryptPasswordEncoder;


    public UserService(UserRepo userRepo, BCryptPasswordEncoder cryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.cryptPasswordEncoder = cryptPasswordEncoder;
    }
    public User createUser(SignupRequest userIn) {
        User user  = new User();
        user.setEmail(userIn.getEmail());
        user.setFirstname(userIn.getFirstname());
        user.setLastname(userIn.getLastname());
        user.setUsername(userIn.getUsername());
        user.setPassword(cryptPasswordEncoder.encode(userIn.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving User {}", userIn.getEmail());
            return userRepo.save(user);
        } catch (Exception ex) {
            LOG.error("Error during registration. {}", ex.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist. Please check credentials");

        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setBio(userDTO.getBio());
        return userRepo.save(user);
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

}
