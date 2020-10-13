package com.marklogic.demo.dataloader.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mock-data")
public class MockDataController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getMockData() {
        Map<String, Object> response = new HashMap<>();

        response.put("ID", 1);
        response.put("FIRST_NAME", "Jane");
        response.put("LAST_NAME", "Doe");
        response.put("EMAIL", "janedoe@clickbank.net");
        response.put("GENDER", "Female");
        response.put("IP_ADDRESS", "1.1.1.1");

        return response;
    }
}
