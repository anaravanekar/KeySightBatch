package com.sereneast.keysight.config;

import com.sereneast.keysight.batch.reader.AccountJdbcItemReader;
import com.sereneast.keysight.batch.listener.AccountJobExecutionListener;
import com.sereneast.keysight.batch.listener.AccountStepExecutionListener;
import com.sereneast.keysight.batch.writer.AccountRestWriter;
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
public class AccountBatchJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Environment environment;

	@Bean
	public AccountJdbcItemReader accountJdbcItemReader(DataSource dataSource){
		AccountJdbcItemReader databaseReader= new AccountJdbcItemReader();
		return databaseReader;
	}

	@Bean
	public AccountRestWriter accountRestWriter() {
		return new AccountRestWriter();
	}

	@Bean
	public AccountJobExecutionListener accountJobExecutionListener() {
		return new AccountJobExecutionListener();
	}

	@Bean
	public AccountStepExecutionListener accountStepExecutionListener(){
		return new AccountStepExecutionListener();
	}

	@Bean("accountTaskExecutor")
	public TaskExecutor accountTaskExecutor(){
		SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(5);
//		SyncTaskExecutor asyncTaskExecutor = new SyncTaskExecutor();
		return asyncTaskExecutor;
	}

	@Bean
	public Step accountBatchJobStep(AccountJdbcItemReader accountJdbcItemReader,AccountRestWriter accountRestWriter,@Qualifier("accountTaskExecutor") TaskExecutor accountTaskExecutor,AccountStepExecutionListener accountStepExecutionListener) {
		return stepBuilderFactory.get("accountBatchJobStep").<OrchestraObject, OrchestraObject>chunk(Integer.valueOf(environment.getProperty("keysight.job.account.chunkSize")))
				.reader(accountJdbcItemReader)
				.processor(null)
				.writer(accountRestWriter)
				.listener(accountStepExecutionListener)
				.taskExecutor(accountTaskExecutor).build();
	}

	@Bean
	public Job accountBatchJob(@Qualifier("accountBatchJobStep")Step accountBatchJobStep, AccountJobExecutionListener accountJobExecutionListener){
		return jobBuilderFactory.get("accountBatchJob")
				.incrementer(new RunIdIncrementer())
				.listener(accountJobExecutionListener)
				.flow(accountBatchJobStep).end().build();
	}

}
