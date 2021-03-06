package com.sereneast.keysight.batch.listener;

import com.sereneast.keysight.model.AccountLastPolledIdStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountJobExecutionListener extends JobExecutionListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountJobExecutionListener.class);

	private static final String ACCOUNT_LAST_POLLED_FILENAME = "ACCOUNT_LAST_POLLED.txt";

	@Autowired
	private AccountLastPolledIdStore accountLastPolledIdStore;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		LOGGER.debug("BEFORE ACCOUNT JOB.....>");
		Path path = Paths.get(ACCOUNT_LAST_POLLED_FILENAME);
		boolean fileExists = Files.exists(path);
		try (Stream<String> lines = Files.lines(!fileExists?Files.createFile(path):path)) {
			String accountLastPolled = lines.collect(Collectors.joining());
			LOGGER.debug("BEFORE ACCOUNT JOB accountLastPolled from file = "+accountLastPolled);
			if(!StringUtils.isEmpty(accountLastPolled)){
				accountLastPolledIdStore.setLastPolledId(accountLastPolled);
			}else{
				accountLastPolledIdStore.setLastPolledId("0");
			}
		} catch (IOException e) {
			LOGGER.error("Error while reading Last Polled Id for Account from File at {}",path);
		}
		LOGGER.debug("BEFORE ACCOUNT JOB.....<");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		LOGGER.debug("AFTER ACCOUNT JOB.....>");
		Path path = Paths.get(ACCOUNT_LAST_POLLED_FILENAME);
		String accountLastPolled = accountLastPolledIdStore.getLastPolledId();
		try{
			LOGGER.debug("AFTER ACCOUNT JOB accountLastPolled to save = "+accountLastPolled);
			Files.deleteIfExists(path);
			Files.createFile(path);
			Files.write(path,accountLastPolled.getBytes());
		} catch (IOException e) {
			LOGGER.error("Error while writing Last Polled Id for Account to File at {}",path);
		}
		LOGGER.debug("AFTER ACCOUNT JOB.....<");
	}
}
