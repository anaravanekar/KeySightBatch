package com.sereneast.keysight.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class AddressStepExecutionListener implements StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOGGER.debug("BEFORE ADDRESS STEP.....> "+stepExecution.getStatus());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.debug("AFTER ADDRESS STEP.....> Status:{} Count:{}",stepExecution.getStatus(),stepExecution.getCommitCount());
        return null;
    }

}
