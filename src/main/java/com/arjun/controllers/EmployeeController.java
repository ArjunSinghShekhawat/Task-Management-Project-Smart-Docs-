package com.arjun.controllers;

import com.arjun.models.Employee;
import com.arjun.models.Employees;
import com.arjun.repositories.EmployeeRepository;
import com.arjun.services.XMLService;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private XMLService xmlService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadXML(@RequestParam("file") MultipartFile file) {
        try {
            // Read file content as String
            String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Parse XML data
            Employees employees = xmlService.parseXML(xmlContent);

            // Save to MongoDB
            employeeRepository.saveAll(employees.getEmployees());

            return ResponseEntity.ok("XML data successfully parsed and stored in MongoDB.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading file: " + e.getMessage());
        } catch (JAXBException e) {
            return ResponseEntity.badRequest().body("XML Parsing Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
