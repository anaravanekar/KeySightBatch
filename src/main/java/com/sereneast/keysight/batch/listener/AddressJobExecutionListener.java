package com.sereneast.keysight.batch.listener;

import com.sereneast.keysight.model.AddressLastPolledIdStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddressJobExecutionListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressJobExecutionListener.class);

    private static final String ADDRESS_LAST_POLLED_FILENAME = "ADDRESS_LAST_POLLED.txt";

    @Autowired
    private AddressLastPolledIdStore addressLastPolledIdStore;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOGGER.debug("BEFORE ADDRESS JOB.....>");
        Path path = Paths.get(ADDRESS_LAST_POLLED_FILENAME);
        boolean fileExists = Files.exists(path);
        try (Stream<String> lines = Files.lines(!fileExists?Files.createFile(path):path)) {
            String addressLastPolled = lines.collect(Collectors.joining());
            LOGGER.debug("BEFORE ADDRESS JOB addressLastPolled from file = "+addressLastPolled);
            if(!StringUtils.isEmpty(addressLastPolled)){
                addressLastPolledIdStore.setLastPolledId(addressLastPolled);
            }else{
                addressLastPolledIdStore.setLastPolledId("0");
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading Last Polled Id for Address from File at {}",path);
        }
        LOGGER.debug("BEFORE ADDRESS JOB.....<");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOGGER.debug("AFTER ADDRESS JOB.....>");
        Path path = Paths.get(ADDRESS_LAST_POLLED_FILENAME);
        String addressLastPolled = addressLastPolledIdStore.getLastPolledId();
        try{
            LOGGER.debug("AFTER ADDRESS JOB addressLastPolled to save = "+addressLastPolled);
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.write(path,addressLastPolled.getBytes());
        } catch (IOException e) {
            LOGGER.error("Error while writing Last Polled Id for Address to File at {}",path);
        }
        LOGGER.debug("AFTER ADDRESS JOB.....<");
    }
}
