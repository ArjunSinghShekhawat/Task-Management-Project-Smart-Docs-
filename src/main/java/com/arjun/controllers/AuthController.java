package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserAlreadyExistsException;
import com.arjun.exceptions.UserException;
import com.arjun.request.LoginRequest;
import com.arjun.request.SignUpRequest;
import com.arjun.responce.AuthResponse;
import com.arjun.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

    private final AuthService authService;


    @Operation(
            summary = "User Sign-Up",
            description = "Registers a new user with the provided details. Returns the created user information."
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "User already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
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

    @Operation(
            summary = "User Sign-In",
            description = "Authenticates a user with their credentials and returns a JWT token if successful."
    )
    @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    @ApiResponse(responseCode = "500", description = "Internal server error")
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
