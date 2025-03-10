package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserException;
import com.arjun.responce.AuthResponse;
import com.arjun.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing user-related operations")
public class UserController {


    private final UserService userService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the user."),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user ID format."),
            @ApiResponse(responseCode = "404", description = "Not Found - The user not found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - An error occurred while updating the user.")
    })
    @Operation(
            summary = "Update a user",
            description = "Update the user information. If the user is not found, returns a 404 Not Found response."
    )
    @PutMapping
    public ResponseEntity<?> updateUserEntityHandler(
            @RequestHeader("Authorization") String jwt,
            @RequestBody UserDto userDto) throws UserException {
        try {
            UserDto updatedUser = userService.updateUser(jwt, userDto);
            if (updatedUser == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the user."),
            @ApiResponse(responseCode = "404", description = "Not Found - The user not found."),
    })
    @Operation(
            summary = "Delete a user",
            description = "Delete a user"
    )
    @DeleteMapping
    public ResponseEntity<AuthResponse>deleteUserEntityHandler(@RequestHeader("Authorization") String jwt) throws UserException {

        boolean isRemove = userService.deleteUser(jwt);
        if(isRemove){
            return new ResponseEntity<>(new AuthResponse("User Delete Successfully",true,null,null),HttpStatus.OK);
        }
        return new ResponseEntity<>(new AuthResponse("User Not Found Successfully",false,null,null),HttpStatus.NOT_FOUND);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user."),
            @ApiResponse(responseCode = "404", description = "Not found - The user not found."),
    })
    @Operation(
            summary = "Find a user",
            description = "Fetch a user"
    )
    @GetMapping
    public ResponseEntity<UserDto>getUserHandler(@RequestHeader("Authorization") String jwt) throws UserException {

        UserDto user = userService.getUser(jwt);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

}
