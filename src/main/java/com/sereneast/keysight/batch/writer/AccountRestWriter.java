package com.sereneast.keysight.batch.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.model.OrchestraObjectList;
import com.sereneast.keysight.rest.client.InvokeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class AccountRestWriter implements ItemWriter<com.sereneast.keysight.model.OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRestWriter.class);

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    @Autowired
    private InvokeService invokeService;

    private static final String successUpdateQuery =
            "UPDATE mdm_account SET interface_status='success', execution_date=:executionDate"+
            " mdm_account_id=:mdmAccountId  where system_id=:systemId AND system_name=:systemName";

    private static final String errorUpdateQuery = "";

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends OrchestraObject> accounts) throws Exception {
/*
        List<Map<String,Object>> accountDataForBatchUpdateSuccess = new ArrayList<>();
         oracleDbNamedParameterJdbcTemplate.batchUpdate(successUpdateQuery, accountDataForBatchUpdateSuccess.toArray(new HashMap[successUpdateQuery.length()]));
*/
        List<LinkedHashMap<String,Object>> accountDataForBatchUpdateSuccess = new ArrayList<>();

        //use rows object for bult insert with rest api
        OrchestraObjectList rows = new OrchestraObjectList();
        rows.setRows((List<OrchestraObject>)accounts);

        Thread.sleep((new Random().nextInt(5)+1)*1000);
        LOGGER.debug("WRITING ACCOUNTS"+new ObjectMapper().writeValueAsString(accounts));

        //Call REST API here and update status to success in db
        for(OrchestraObject account: accounts){
           // invokeService.insertAccount(account,"localhost:8080","admin","admin",account.getContent().get("SystemId").getContent().toString(),account.getContent().get("SystemName").getContent().toString());
            LinkedHashMap<String,Object> batchParams = new LinkedHashMap<>();
            batchParams.put("systemId",account.getContent().get("SystemId").getContent());
            accountDataForBatchUpdateSuccess.add(batchParams);
           LOGGER.debug(Thread.currentThread().getName() + " Writing account " + account.getContent().get("SystemId").getContent());
        }
        LinkedHashMap<String,Object>[] map = new LinkedHashMap[accountDataForBatchUpdateSuccess.size()];
        oracleDbNamedParameterJdbcTemplate.batchUpdate("UPDATE mdm_account SET interface_status='success' where system_id=:systemId",accountDataForBatchUpdateSuccess.toArray(map));
    }
}
