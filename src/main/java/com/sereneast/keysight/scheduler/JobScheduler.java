package com.sereneast.keysight.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    private final JobLauncher jobLauncher;
    private final Job accountJob;
//    private final Job addressJob;

    @Autowired
    public JobScheduler(JobLauncher jobLauncher,@Qualifier("accountBatchJob")Job accountJob) {
        this.jobLauncher = jobLauncher;
        this.accountJob = accountJob;
//        this.addressJob = addressJob;
    }

    @Scheduled(cron = "${keysight.job.account.cron}")
    public void runAccountJob(){
        try {
            JobParametersBuilder parametersBuilder = new JobParametersBuilder();
            parametersBuilder.addLong("time",System.currentTimeMillis());
            jobLauncher.run(accountJob,parametersBuilder.toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
           LOGGER.error("Error scheduling Account Job",e);
        }
    }

/*    @Scheduled(cron = "${keysight.job.address.cron.expression}")
    public void runAddressJob(){
        try {
            jobLauncher.run(addressJob,new JobParametersBuilder().toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            LOGGER.error("Error scheduling Address Job",e);
        }
    }*/

}
