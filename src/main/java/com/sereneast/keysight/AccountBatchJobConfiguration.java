package com.sereneast.keysight;

import com.sereneast.keysight.batch.listener.JobCompletionNotificationListener;
import com.sereneast.keysight.batch.processor.AccountProcessor;
import com.sereneast.keysight.batch.writer.AccountRestWriter;
import com.sereneast.keysight.repo.model.Account;
import com.sereneast.keysight.util.ResultSetToHashMapRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;


@Configuration
@EnableBatchProcessing
public class AccountBatchJobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Resource
	private Environment environment ;

	@ConfigurationProperties(prefix = "spring.job.datasource")
	@Bean(name="jobDataSource")
	public DataSource jobDataSource()  {
/*		if (hsqlServerBean.isServerRunning()) {
			return DataSourceBuilder.create()
			.driverClassName(environment.getProperty("spring.job.datasource.driverClassName"))
			.url(environment.getProperty("spring.job.datasource.url"))
			.username(environment.getProperty("spring.job.datasource.username"))
			.password(environment.getProperty("spring.job.datasource.password"))
			.build();
		}*/

		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setDriverClassName(environment.getProperty("spring.job.datasource.driverClassName"));
		ds.setUrl(environment.getProperty("spring.job.datasource.url"));
		ds.setUsername(environment.getProperty("spring.job.datasource.username"));
		ds.setPassword(environment.getProperty("spring.job.datasource.password"));
		ds.setInitialSize(2);
		ds.setMaxActive(10);
		ds.setMaxIdle(5);
		ds.setMinIdle(2);
		ds.setMaxWait(2000);
		return ds;


/*		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getProperty("spring.job.datasource.driverClassName"));
		dataSource.setUrl(environment.getProperty("spring.job.datasource.url"));
		dataSource.setUsername(environment.getProperty("spring.job.datasource.username"));
		dataSource.setPassword(environment.getProperty("spring.job.datasource.password"));
		return dataSource;*/
	}

	@Bean
	public JdbcCursorItemReader<Map<String,Object>> jdbcCursorItemReader() {
		JdbcCursorItemReader<Map<String,Object>> databaseReader= new JdbcCursorItemReader<Map<String,Object>>();
		databaseReader.setDataSource(jobDataSource());
		databaseReader.setSql(environment.getProperty("keysight.accountQuery"));
		databaseReader.setRowMapper(new ResultSetToHashMapRowMapper());
		return new JdbcCursorItemReader();
	}

	@Bean
	public AccountRestWriter accountRestWriter() {
		return new AccountRestWriter();
	}

	@Bean
	public AccountProcessor accountProcessor() {
		return new AccountProcessor();
	}

	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionNotificationListener();
	}

	@Bean
	public Job buildJob() {
		return jobBuilderFactory.get("Account ETL Job").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(buildStep()).end().build();
	}

	@Bean
	public TaskExecutor buildTaskExecutor(){
		SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
		asyncTaskExecutor.setConcurrencyLimit(5);
		return asyncTaskExecutor;
	}

	@Bean
	public Step buildStep() {
		return stepBuilderFactory.get("Extract -> Transform -> Load").<Map<String,Object>, Account> chunk(1000)
				.reader(jdbcCursorItemReader())
				.processor(accountProcessor())
				.writer(accountRestWriter())
				.taskExecutor(buildTaskExecutor()).build();
	}

}
