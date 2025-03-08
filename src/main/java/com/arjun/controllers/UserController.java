package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserException;
import com.arjun.responce.AuthResponse;
import com.arjun.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PutMapping
    public ResponseEntity<UserDto>updateUserEntityHandler(@RequestHeader("Authorization") String jwt,
                                                          @RequestBody UserDto userDto) throws UserException {
            UserDto updatedUser = userService.updateUser(jwt, userDto);
            if (updatedUser == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<AuthResponse>updateUserEntityHandler(@RequestHeader("Authorization") String jwt) throws UserException {

        boolean isRemove = userService.deleteUser(jwt);
        if(isRemove){
            return new ResponseEntity<>(new AuthResponse("User Delete Successfully",true,null,null),HttpStatus.OK);
        }
        return new ResponseEntity<>(new AuthResponse("User Delete Successfully",false,null,null),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserDto>getUserHandler(@RequestHeader("Authorization") String jwt) throws UserException {

        UserDto user = userService.getUser(jwt);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

}
