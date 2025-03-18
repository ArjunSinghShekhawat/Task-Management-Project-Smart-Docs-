package com.arjun.controllers;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserException;
import com.arjun.services.ExcelHelper;
import com.arjun.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;


@RestController
@RequestMapping("/auth")
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

    // 🔹 Download all users as an Excel file
    @GetMapping("/export-users")
    public ResponseEntity<InputStreamResource> exportUsersToExcel() {
        ByteArrayInputStream in = userService.exportUsersToExcel(); // Fetch Excel data

        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }


    // 🔹 Upload an Excel file and save users in the database
    @PostMapping("/import/excel")
    public ResponseEntity<String> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        userService.importUsersFromExcel(file);
        return ResponseEntity.ok("Users imported successfully!");
    }

}
