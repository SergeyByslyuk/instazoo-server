package com.sergeytechnologies.instazoo.web;

import com.sergeytechnologies.instazoo.dto.UserDTO;
import com.sergeytechnologies.instazoo.entity.User;
import com.sergeytechnologies.instazoo.facade.UserFacade;
import com.sergeytechnologies.instazoo.service.UserService;
import com.sergeytechnologies.instazoo.service.validations.ResponseErrorValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserFacade userFacade;

    private final ResponseErrorValidation responseErrorValidation;

    public UserController(UserService userService, UserFacade userFacade, ResponseErrorValidation responseErrorValidation) {
        this.userService = userService;
        this.userFacade = userFacade;
        this.responseErrorValidation = responseErrorValidation;
    }

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDT0(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = userFacade.userToUserDT0(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        User user = userService.updateUser(userDTO, principal);

        UserDTO userUpdated = userFacade.userToUserDT0(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);

    }
}
