package com.sereneast.keysight.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class AccountStepExecutionListener implements StepExecutionListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOGGER.debug("BEFORE ACCOUNT STEP.....> "+stepExecution.getStatus());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.debug("AFTER ACCOUNT STEP.....> Status:{} Count:{}",stepExecution.getStatus(),stepExecution.getCommitCount());
        return null;
    }
}
