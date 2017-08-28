package com.sereneast.keysight.batch;

import com.sereneast.keysight.config.properties.AccountJobProperties;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.AccountLastPolledIdStore;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountJdbcItemReader<OrchestraObject> implements ItemReader<OrchestraObject> {

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
                if (StringUtils.isEmpty(lastId)) {
                    lastId = "0";
                    accountLastPolledIdStore.setLastPolledId("0");
                }
                ((JdbcTemplate) oracleDbNamedParameterJdbcTemplate.getJdbcOperations()).setMaxRows(accountJobProperties.getFetchSize());
                List<OrchestraObject> resultList = oracleDbNamedParameterJdbcTemplate.query(applicationProperties.getQueries().get("getPendingAccounts").replaceAll(":systemId", lastId), (RowMapper<OrchestraObject>) resultSetToOrachestraObjectRowMapper);
                items = new LinkedBlockingQueue<>(resultList);
                LOGGER.debug(Thread.currentThread().getName() + " | Fetched Items -" + items.size());
            }
            return items.poll();
        }
    }
}
