package com.sereneast.keysight.scheduler;

import com.sereneast.keysight.config.properties.ApplicationProperties;
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

    private final ApplicationProperties applicationProperties;
    private final JobLauncher jobLauncher;
    private final Job job;

    @Autowired
    public JobScheduler(ApplicationProperties applicationProperties, JobLauncher jobLauncher,@Qualifier("accountBatchJob") Job job) {
        this.applicationProperties = applicationProperties;
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @Scheduled(cron = "${cron.expression}")
    public void runAccountJob(){
        try {
            jobLauncher.run(job,new JobParametersBuilder().toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

}
