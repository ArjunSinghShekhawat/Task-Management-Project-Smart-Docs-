package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserAlreadyExistsException;
import com.arjun.exceptions.UserException;
import com.arjun.request.LoginRequest;
import com.arjun.request.SignUpRequest;
import com.arjun.responce.AuthResponse;
import com.arjun.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;


    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUpHandler(@Valid @RequestBody SignUpRequest request) {
        try {
            // Attempt to sign up the user and retrieve the saved UserDto
            UserDto savedUser = authService.signUp(request);

            // If savedUser is null, indicating failure, return BAD_REQUEST
            if (savedUser == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            // Successfully created the user, return CREATED status
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            // Specific exception handling if the user already exists
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Conflict status if user already exists
        } catch (Exception e) {
            // Handle any generic exceptions that occur (e.g., database issues)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse>loginHandler(@Valid  @RequestBody LoginRequest request){
        log.info("Attempting to sign in user with email: {}", request.getEmail());
        try {
            // Call the service to authenticate the user and generate a JWT token
            AuthResponse authResponse = authService.signIn(request);

            // Check if the authentication was successful and log it
            if (authResponse != null) {
                authResponse.setStatus(true);
                log.info("User with username: {} successfully signed in.", request.getEmail());
                return new ResponseEntity<>(authResponse, HttpStatus.OK);
            } else {
                log.warn("Invalid credentials for username: {}", request.getEmail());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("An error occurred during sign-in attempt for username: {}", request.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
