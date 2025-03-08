package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/all-user")
    public ResponseEntity<Page<UserDto>>getAllUser(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        return new ResponseEntity<>(userService.getAllUsers(page,size), HttpStatus.OK);
    }
}
