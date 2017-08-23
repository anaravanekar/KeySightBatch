package com.sereneast.keysight.batch.processor;

import com.sereneast.keysight.repo.model.Account;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.Objects;

public class AccountProcessor implements ItemProcessor<Map<String,Object>, Account> {

	@Override
	public Account process(final Map<String,Object> dbRecordAsMap) throws Exception {
		//TODO: convert db record in java map format to Account with address object here
		final Account account = new Account();
		account.setArticleId(dbRecordAsMap.get("article_id")!=null?(Integer)dbRecordAsMap.get("article_id"):null);
		account.setTitle(Objects.toString(dbRecordAsMap.get("title"),null));
		account.setCategory(Objects.toString(dbRecordAsMap.get("category"),null));
		return account;
	}

}
