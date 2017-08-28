package com.sereneast.keysight.config;

import com.sereneast.keysight.batch.AccountJdbcItemReader;
import com.sereneast.keysight.batch.listener.AccountJobExecutionListener;
import com.sereneast.keysight.batch.writer.AccountRestWriter;
import com.sereneast.keysight.config.properties.AccountJobProperties;
import com.sereneast.keysight.config.properties.AddressJobProperties;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.OrchestraObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.sql.DataSource;

@SuppressWarnings("ALL")
@Configuration
@EnableBatchProcessing
@EnableScheduling
@EnableConfigurationProperties({AccountJobProperties.class, AddressJobProperties.class, ApplicationProperties.class})
public class AccountBatchJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Environment environment;

	@Bean
	public AccountJdbcItemReader<OrchestraObject> accountJdbcItemReader(DataSource dataSource){
		AccountJdbcItemReader<OrchestraObject> databaseReader= new AccountJdbcItemReader<OrchestraObject>();
//		databaseReader.setDataSource(dataSource);
		return databaseReader;
	}

	@Bean
	public AccountRestWriter accountRestWriter() {
		return new AccountRestWriter();
	}

	@Bean
	public JobExecutionListener jobExecutionListener() {
		return new AccountJobExecutionListener();
	}

	@Bean
	public TaskExecutor taskExecutor(){
		SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(2);
//		SyncTaskExecutor asyncTaskExecutor = new SyncTaskExecutor();
		return asyncTaskExecutor;
	}

	@Bean
	public Step accountBatchJobStep(AccountJdbcItemReader accountJdbcItemReader,AccountRestWriter accountRestWriter,TaskExecutor taskExecutor) {
		return stepBuilderFactory.get("accountBatchJobStep").<OrchestraObject, OrchestraObject>chunk(Integer.valueOf(environment.getProperty("keysight.job.account.chunkSize")))
				.reader(accountJdbcItemReader)
				.processor(null)
				.writer(accountRestWriter)
				.taskExecutor(taskExecutor).build();
	}

	@Bean
	public Job accountBatchJob(@Qualifier("accountBatchJobStep")Step accountBatchJobStep, JobExecutionListener jobExecutionListener){
		return jobBuilderFactory.get("accountBatchJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener)
				.flow(accountBatchJobStep).end().build();
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

	@Bean
	public NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate(DataSource dataSource){
		return new NamedParameterJdbcTemplate(dataSource);
	}

}
