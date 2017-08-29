package com.sereneast.keysight.config;

import com.sereneast.keysight.batch.listener.AddressJobExecutionListener;
import com.sereneast.keysight.batch.listener.AddressStepExecutionListener;
import com.sereneast.keysight.batch.reader.AddressJdbcItemReader;
import com.sereneast.keysight.batch.writer.AddressRestWriter;
import com.sereneast.keysight.model.OrchestraObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@SuppressWarnings("ALL")
@Configuration
public class AddressBatchJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Environment environment;

    @Bean
    public AddressJdbcItemReader addressJdbcItemReader(DataSource dataSource){
        AddressJdbcItemReader databaseReader= new AddressJdbcItemReader();
        return databaseReader;
    }

    @Bean
    public AddressRestWriter addressRestWriter() {
        return new AddressRestWriter();
    }

    @Bean
    public AddressJobExecutionListener addressJobExecutionListener() {
        return new AddressJobExecutionListener();
    }

    @Bean
    public AddressStepExecutionListener addressStepExecutionListener(){
        return new AddressStepExecutionListener();
    }

    @Bean("addressTaskExecutor")
    public TaskExecutor addressTaskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(5);
//		SyncTaskExecutor asyncTaskExecutor = new SyncTaskExecutor();
        return asyncTaskExecutor;
    }

    @Bean
    public Step addressBatchJobStep(AddressJdbcItemReader addressJdbcItemReader, AddressRestWriter addressRestWriter, @Qualifier("addressTaskExecutor") TaskExecutor addressTaskExecutor, AddressStepExecutionListener addressStepExecutionListener) {
        return stepBuilderFactory.get("addressBatchJobStep").<OrchestraObject, OrchestraObject>chunk(Integer.valueOf(environment.getProperty("keysight.job.address.chunkSize")))
                .reader(addressJdbcItemReader)
                .processor(null)
                .writer(addressRestWriter)
                .listener(addressStepExecutionListener)
                .taskExecutor(addressTaskExecutor).build();
    }

    @Bean
    public Job addressBatchJob(@Qualifier("addressBatchJobStep")Step addressBatchJobStep, AddressJobExecutionListener addressJobExecutionListener){
        return jobBuilderFactory.get("addressBatchJob")
                .incrementer(new RunIdIncrementer())
                .listener(addressJobExecutionListener)
                .flow(addressBatchJobStep).end().build();
    }

}
