package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Management", description = "APIs for managing administrative tasks")
public class AdminController {

    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Fetches a paginated list of users with default values for page and size."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    @GetMapping("/all-user")
    public ResponseEntity<Page<UserDto>>getAllUser(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        return new ResponseEntity<>(userService.getAllUsers(page,size), HttpStatus.OK);
    }
}
