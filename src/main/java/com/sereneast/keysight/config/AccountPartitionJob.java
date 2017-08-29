package com.sereneast.keysight.config;

import com.sereneast.keysight.batch.writer.AccountRestWriter;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.util.MyPartitioner;
import com.sereneast.keysight.util.ResultSetToOrachestraObjectRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

//@SuppressWarnings("ALL")
//@Configuration
//@EnableBatchProcessing
//@EnableScheduling
//@EnableConfigurationProperties({AccountJobProperties.class, AddressJobProperties.class, ApplicationProperties.class})
public class AccountPartitionJob {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Environment environment;

    @Bean
    @StepScope
    @DependsOn({"applicationProperties","resultSetToOrachestraObjectRowMapper"})
    public JdbcPagingItemReader<OrchestraObject> jdbcPagingItemReader(@Value("#{stepExecutionContext[startingIndex]}")Object start,@Value("#{stepExecutionContext[endingIndex]}")Object end, DataSource dataSource, @Qualifier("resultSetToOrachestraObjectRowMapper")ResultSetToOrachestraObjectRowMapper resultSetToOrachestraObjectRowMapper){
        JdbcPagingItemReader<OrchestraObject> jdbcPagingItemReader = new JdbcPagingItemReader<OrchestraObject>();
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setRowMapper(resultSetToOrachestraObjectRowMapper);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("startingIndex",start);
        map.put("endingIndex",end);
        jdbcPagingItemReader.setParameterValues(map);
        jdbcPagingItemReader.setPageSize(5);
        return jdbcPagingItemReader;
    }

    @Bean
    @StepScope
    public AccountRestWriter accountRestWriter() {
        return new AccountRestWriter();
    }

    @Bean
    public MyPartitioner myPartitioner(){
        MyPartitioner partitioner = new MyPartitioner();
        return partitioner;
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(2);
//		SyncTaskExecutor asyncTaskExecutor = new SyncTaskExecutor();
        return asyncTaskExecutor;
    }

    @Bean
    public Step slave(JdbcPagingItemReader<OrchestraObject> jdbcPagingItemReader, AccountRestWriter accountRestWriter) {
        return this.stepBuilderFactory.get("slave")
                .<OrchestraObject, OrchestraObject>chunk(20)
                .reader(jdbcPagingItemReader)
                .writer(accountRestWriter)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler taskExecutorPartitionHandler(TaskExecutor taskExecutor){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        taskExecutorPartitionHandler.setGridSize(4);
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step master(Partitioner partitioner,TaskExecutorPartitionHandler taskExecutorPartitionHandler) {
        return this.stepBuilderFactory.get("master")
                .partitioner("slave", partitioner)
                .partitionHandler(taskExecutorPartitionHandler)
                .build();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate(DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public Job accountBatchJob(@Qualifier("master")Step master){
        return jobBuilderFactory.get("accountBatchJob")
                .start(master)
                .build();
    }
}
