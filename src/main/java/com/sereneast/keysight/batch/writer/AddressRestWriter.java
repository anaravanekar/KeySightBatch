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

public class AddressRestWriter implements ItemWriter<OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressRestWriter.class);

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    @Autowired
    private InvokeService invokeService;

    private static final String successUpdateQuery =
            "UPDATE mdm_address_address SET interface_status='success', execution_date=:executionDate"+
                    " mdm_address_id=:mdmAddressId  where system_id=:systemId AND system_name=:systemName";

    private static final String errorUpdateQuery = "";

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends OrchestraObject> addresses) throws Exception {
/*
        List<Map<String,Object>> addressDataForBatchUpdateSuccess = new ArrayList<>();
         oracleDbNamedParameterJdbcTemplate.batchUpdate(successUpdateQuery, addressDataForBatchUpdateSuccess.toArray(new HashMap[successUpdateQuery.length()]));
*/
        List<LinkedHashMap<String,Object>> addressDataForBatchUpdateSuccess = new ArrayList<>();

        //use rows object for bult insert with rest api
        OrchestraObjectList rows = new OrchestraObjectList();
        rows.setRows((List<OrchestraObject>)addresses);

        Thread.sleep((new Random().nextInt(5)+1)*1000);
        LOGGER.debug("WRITING ADDRESSES "+new ObjectMapper().writeValueAsString(addresses));

        //Call REST API here and update status to success in db
        for(OrchestraObject address: addresses){
            // invokeService.insertAddress(address,"localhost:8080","admin","admin",address.getContent().get("SystemId").getContent().toString(),address.getContent().get("SystemName").getContent().toString());
            LinkedHashMap<String,Object> batchParams = new LinkedHashMap<>();
            batchParams.put("systemId",address.getContent().get("SystemId").getContent());
            addressDataForBatchUpdateSuccess.add(batchParams);
            LOGGER.debug(Thread.currentThread().getName() + " Writing address " + address.getContent().get("SystemId").getContent());
        }
        LinkedHashMap<String,Object>[] map = new LinkedHashMap[addressDataForBatchUpdateSuccess.size()];
        oracleDbNamedParameterJdbcTemplate.batchUpdate("UPDATE mdm_account_address SET interface_status='success' where system_id=:systemId",addressDataForBatchUpdateSuccess.toArray(map));
    }
}
