package com.ubb.minisgbdserver.controller;

import com.ubb.minisgbdserver.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/database")
public class DatabaseController {

    private DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostMapping(value = "/")
    public ResponseEntity<String> addDatabase(@RequestBody String name) throws Exception {
        databaseService.createDatabase(name);
        return new ResponseEntity<>("Database added!", HttpStatus.CREATED);
    }

}
