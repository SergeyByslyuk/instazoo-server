package com.sergeytechnologies.instazoo.web;

import com.sergeytechnologies.instazoo.payload.request.LoginRequest;
import com.sergeytechnologies.instazoo.payload.request.SignupRequest;
import com.sergeytechnologies.instazoo.payload.response.JWTokenSuccessResponse;
import com.sergeytechnologies.instazoo.payload.response.MessageResponse;
import com.sergeytechnologies.instazoo.security.JWTTokenProvider;
import com.sergeytechnologies.instazoo.security.SecurityConstants;
import com.sergeytechnologies.instazoo.service.UserService;
import com.sergeytechnologies.instazoo.validations.ResponseErrorValidation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ResponseErrorValidation responseErrorValidation;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, ResponseErrorValidation errorValidation,
                          UserService userService, JWTTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.responseErrorValidation = errorValidation;
        this.userService = userService;
        this.jwtTokenProvider = tokenProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTokenSuccessResponse(true, jwt));

    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;
        userService.createUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }



}
