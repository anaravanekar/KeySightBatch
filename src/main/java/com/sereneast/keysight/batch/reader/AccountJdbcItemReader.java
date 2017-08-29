package com.sereneast.keysight.batch.reader;

import com.sereneast.keysight.config.properties.AccountJobProperties;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.AccountLastPolledIdStore;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.util.ResultSetToOrachestraObjectRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountJdbcItemReader implements ItemReader<OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountJdbcItemReader.class);

    @Autowired
    private AccountLastPolledIdStore accountLastPolledIdStore;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private AccountJobProperties accountJobProperties;

    @Autowired
    private ResultSetToOrachestraObjectRowMapper resultSetToOrachestraObjectRowMapper;

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    private Queue<OrchestraObject> items = new LinkedBlockingQueue<>();

    private Integer readerLock = 5;

    @Override
    public OrchestraObject read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        synchronized(readerLock) {
            if (items == null || items.isEmpty()) {
                String lastId = accountLastPolledIdStore.getLastPolledId();
                LOGGER.debug("items is null. fetching records from db by lastId = {}",lastId);
                ((JdbcTemplate) oracleDbNamedParameterJdbcTemplate.getJdbcOperations()).setMaxRows(accountJobProperties.getFetchSize());
                List<OrchestraObject> resultList = oracleDbNamedParameterJdbcTemplate.query(applicationProperties.getQueries().get("getPendingAccounts").replaceAll(":systemId",lastId), (RowMapper<OrchestraObject>) resultSetToOrachestraObjectRowMapper);
                LOGGER.debug(Thread.currentThread().getName() + " | Fetched Items from db count= " + resultList.size());
                for(OrchestraObject account: resultList){
                    LOGGER.debug(Thread.currentThread().getName() + " Fetched account from db " + account.getContent().get("SystemId").getContent());
                }
                if(resultList!=null && !resultList.isEmpty()) {
                    lastId = resultList.get(resultList.size()-1).getContent().get("SystemId").getContent().toString();
                    accountLastPolledIdStore.setLastPolledId(lastId);
                    items = new LinkedBlockingQueue<>(resultList);
                }
            }
            OrchestraObject objToReturn = items.poll();
            if(objToReturn!=null)
                LOGGER.debug(Thread.currentThread().getName() + " Reading account from reader " + objToReturn.getContent().get("SystemId").getContent());
            return objToReturn;
        }
    }
}
