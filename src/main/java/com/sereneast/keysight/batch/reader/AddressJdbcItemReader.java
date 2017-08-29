package com.sereneast.keysight.batch.reader;

import com.sereneast.keysight.config.properties.AddressJobProperties;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.AddressLastPolledIdStore;
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

public class AddressJdbcItemReader implements ItemReader<OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressJdbcItemReader.class);

    @Autowired
    private AddressLastPolledIdStore addressLastPolledIdStore;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private AddressJobProperties addressJobProperties;

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
                String lastId = addressLastPolledIdStore.getLastPolledId();
                LOGGER.debug("items is null. fetching records from db by lastId = {}",lastId);
                ((JdbcTemplate) oracleDbNamedParameterJdbcTemplate.getJdbcOperations()).setMaxRows(addressJobProperties.getFetchSize());
                List<OrchestraObject> resultList = oracleDbNamedParameterJdbcTemplate.query(applicationProperties.getQueries().get("getPendingAddresses").replaceAll(":systemId",lastId), (RowMapper<OrchestraObject>) resultSetToOrachestraObjectRowMapper);
                LOGGER.debug(Thread.currentThread().getName() + " | Fetched Items from db count= " + resultList.size());
                for(OrchestraObject address: resultList){
                    LOGGER.debug(Thread.currentThread().getName() + " Fetched address from db " + address.getContent().get("SystemId").getContent());
                }
                if(resultList!=null && !resultList.isEmpty()) {
                    lastId = resultList.get(resultList.size()-1).getContent().get("SystemId").getContent().toString();
                    addressLastPolledIdStore.setLastPolledId(lastId);
                    items = new LinkedBlockingQueue<>(resultList);
                }
            }
            OrchestraObject objToReturn = items.poll();
            if(objToReturn!=null)
                LOGGER.debug(Thread.currentThread().getName() + " Reading address from reader " + objToReturn.getContent().get("SystemId").getContent());
            return objToReturn;
        }
    }

}
