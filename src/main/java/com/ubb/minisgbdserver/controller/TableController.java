package com.ubb.minisgbdserver.controller;

import com.ubb.minisgbdserver.dto.TableDTO;
import com.ubb.minisgbdserver.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/table")
public class TableController {

    TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping(value = "/")
    public ResponseEntity<String> addTable(@RequestBody TableDTO tableDTO) throws Exception {
        if (tableService.createTable(tableDTO))
            return new ResponseEntity<>("Table added.", HttpStatus.CREATED);
        return new ResponseEntity<>("Table could not be added.", HttpStatus.BAD_REQUEST);
    }

}
