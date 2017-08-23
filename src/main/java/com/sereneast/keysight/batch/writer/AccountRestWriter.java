package com.sereneast.keysight.batch.writer;

import com.sereneast.keysight.repo.model.Account;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class AccountRestWriter implements ItemWriter<Account>{

    @Override
    public void write(List<? extends Account> accounts) throws Exception {
        //TODO: iterate over list of accounts and call rest web service using client (InvokeService) here
        accounts.forEach(account->{
            
        });
    }
}
