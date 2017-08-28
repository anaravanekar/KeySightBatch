package com.sereneast.keysight.config;

import com.sereneast.keysight.batch.listener.JobCompletionNotificationListener;
import com.sereneast.keysight.batch.writer.AccountRestWriter;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.util.ResultSetToHashMapRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;
import java.util.Map;

@SuppressWarnings("ALL")
@Configuration
@Import({DataSourceConfiguration.class})
public class AccountBatchJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@ConfigurationProperties(prefix = "job.account")
	@Bean
	public JdbcCursorItemReader<Map<String,Object>> jdbcCursorItemReader(DataSource dataSource) {
		JdbcCursorItemReader<Map<String,Object>> databaseReader= new JdbcCursorItemReader<Map<String,Object>>();
		databaseReader.setDataSource(dataSource);
		databaseReader.setRowMapper(new ResultSetToHashMapRowMapper());
		return new JdbcCursorItemReader();
	}

	@Bean
	public AccountRestWriter accountRestWriter() {
		return new AccountRestWriter();
	}

	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionNotificationListener();
	}

	@Bean(name="accountBatchJob")
	public Job buildJob(@Qualifier("oracleDbDataSource")DataSource dataSource) {
		return jobBuilderFactory.get("ACCOUNTS_DB_TO_REST").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(buildStep(dataSource)).end().build();
	}

	@Bean
	public TaskExecutor buildTaskExecutor(){
		SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
		asyncTaskExecutor.setConcurrencyLimit(5);
		return asyncTaskExecutor;
	}

	@Bean
	public Step buildStep(DataSource dataSource) {
		return stepBuilderFactory.get("Extract -> Transform -> Load").<Map<String,Object>, OrchestraObject> chunk(1000)
				.reader(jdbcCursorItemReader(dataSource))
				.processor(null)
				.writer(accountRestWriter())
				.taskExecutor(buildTaskExecutor()).build();
	}

}
