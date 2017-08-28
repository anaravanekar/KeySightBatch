package com.sereneast.keysight.config;

import com.sereneast.keysight.batch.listener.AccountJobExecutionListener;
import com.sereneast.keysight.batch.writer.AccountRestWriter;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.util.ResultSetToOrachestraObjectRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

/*@SuppressWarnings("ALL")
@Configuration
@Import({DataSourceConfiguration.class})*/
public class AddressBatchJobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Environment environment;

    @Bean
    public JdbcCursorItemReader<OrchestraObject> jdbcCursorItemReader(DataSource dataSource) {
        JdbcCursorItemReader<OrchestraObject> databaseReader= new JdbcCursorItemReader<OrchestraObject>();
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(environment.getProperty("keysight.queries.getPendingAccountAddresses"));
        databaseReader.setFetchSize(Integer.valueOf(environment.getProperty("keysight.job.address.fetchSize")));
        databaseReader.setRowMapper(new ResultSetToOrachestraObjectRowMapper());
        return new JdbcCursorItemReader();
    }

    @Bean
    public AccountRestWriter accountRestWriter() {
        return new AccountRestWriter();
    }

    @Bean
    public JobExecutionListener listener() {
        return new AccountJobExecutionListener();
    }

    @Bean(name="addressBatchJob")
    @DependsOn("oracleDbDataSource")
    public Job buildJob(@Qualifier("oracleDbDataSource")DataSource dataSource) {
        return jobBuilderFactory.get(environment.getProperty("keysight.job.address.name")).incrementer(new RunIdIncrementer()).listener(listener())
                .flow(buildStep(dataSource)).end().build();
    }

    @Bean
    public TaskExecutor buildTaskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor(environment.getProperty("keysight.job.address.name")+"_STEP");
        asyncTaskExecutor.setConcurrencyLimit(5);
        return asyncTaskExecutor;
    }

    @Bean
    public Step buildStep(DataSource dataSource) {
        return stepBuilderFactory.get("Extract -> Transform -> Load").<OrchestraObject, OrchestraObject>chunk(Integer.valueOf(environment.getProperty("keysight.job.address.chunkSize")))
                .reader(jdbcCursorItemReader(dataSource))
                .processor(null)
                .writer(accountRestWriter())
                .taskExecutor(buildTaskExecutor()).build();
    }
}
