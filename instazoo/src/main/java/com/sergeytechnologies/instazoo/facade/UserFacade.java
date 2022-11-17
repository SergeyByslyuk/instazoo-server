package com.sergeytechnologies.instazoo.facade;

import com.sergeytechnologies.instazoo.dto.UserDTO;
import com.sergeytechnologies.instazoo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDTO userToUserDT0(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        userDTO.setUsername(user.getUsername());
        userDTO.setBio(user.getBio());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

}
