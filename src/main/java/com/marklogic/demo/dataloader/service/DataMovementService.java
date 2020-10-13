package com.marklogic.demo.dataloader.service;

import java.util.Collection;
import java.util.UUID;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.demo.dataloader.config.MarkLogicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataMovementService {

    private final Logger logger = LoggerFactory.getLogger(DataMovementService.class);

    private final MarkLogicConfiguration markLogicConfiguration;

    private final DatabaseClient databaseClient;

    @Autowired
    public DataMovementService(MarkLogicConfiguration markLogicConfiguration, DatabaseClient databaseClient) {
        this.markLogicConfiguration = markLogicConfiguration;
        this.databaseClient = databaseClient;
    }

    public void load(String jobId, String additionalCollection, Collection<String> documents) {
        try {

            final DataMovementManager dataMovementManager = this.databaseClient.newDataMovementManager();

            logger.info("Creating WriteBatcher wtih " + this.markLogicConfiguration.getThreads() + " threads and a batch size of " + this.markLogicConfiguration.getBatch());

            String collection = "demo_load_" + jobId;

            final WriteBatcher writeBatcher = dataMovementManager
                    .newWriteBatcher()
                    .withJobName("Demo Load " + jobId)
                    .withBatchSize(this.markLogicConfiguration.getBatch())
                    .withThreadCount(this.markLogicConfiguration.getThreads())
                    .onBatchSuccess((batch -> {
                        for (WriteEvent event : batch.getItems()) {
                            logger.info("WriteEvent: " + event.getTargetUri());
                        }
                    }))
                    .onBatchFailure((batch, throwable) -> {
                        logger.error(throwable.getMessage());
                    });

            final JobTicket jobTicket = dataMovementManager.startJob(writeBatcher);

            String[] collections = {collection, additionalCollection};

            DocumentMetadataHandle documentMetadataHandle = new DocumentMetadataHandle().withCollections(collections);

            for (String document : documents) {
                writeBatcher.add(
                        "/demo-data/" + UUID.randomUUID().toString() + ".json",
                        documentMetadataHandle,
                        new StringHandle(document).withFormat(Format.JSON)
                );
            }

            writeBatcher.flushAndWait();
            dataMovementManager.stopJob(jobTicket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}