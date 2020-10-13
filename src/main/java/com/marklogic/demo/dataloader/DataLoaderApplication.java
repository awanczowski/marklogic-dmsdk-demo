package com.marklogic.demo.dataloader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.demo.dataloader.service.DataMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DataLoaderApplication implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DataMovementService dataMovementService;

    @Autowired
    DataSource dataSource;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${server.port}")
    private String serverPort;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DataLoaderApplication.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run();
    }

    @Override
    public void run(String... args) throws Exception {
        loadViaRest();
        loadViaSQL();
    }

    private void loadViaRest() {
        logger.info("Processing REST call integration...");

        List<String> documents = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:" + serverPort + "/api/v1/mock-data";
        ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            documents.add(response.getBody());
            String jobId = UUID.randomUUID().toString();
            this.dataMovementService.load(jobId, "mock-data-rest-load", documents);
        }
    }

    private void loadViaSQL() {
        logger.info("Processing REST call integration...");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);

        List result = jdbcTemplate.queryForList("SELECT * FROM MOCK_DATA");

        if (result != null) {
            List<String> documents = (List<String>) result.stream().map(m -> {
                try {
                    return this.objectMapper.writeValueAsString(m);
                } catch (JsonProcessingException e) {
                    logger.error("Unable to serialize SQL result", e);
                }
                return null;
            }).collect(Collectors.toList());

            String jobId = UUID.randomUUID().toString();
            this.dataMovementService.load(jobId, "mock-data-sql-load", documents);
        }
    }
}
