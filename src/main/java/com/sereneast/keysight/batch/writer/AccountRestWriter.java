package com.sereneast.keysight.batch.writer;

import com.sereneast.keysight.model.OrchestraObject;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRestWriter implements ItemWriter<com.sereneast.keysight.model.OrchestraObject> {

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    private static final String successUpdateQuery =
            "UPDATE mdm_account SET interface_status='success', execution_date=:executionDate"+
            " mdm_account_id=:mdmAccountId  where system_id=:systemId AND system_name=:systemName";

    private static final String errorUpdateQuery = "";

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends OrchestraObject> accounts) throws Exception {
        List<Map<String,Object>> accountDataForBatchUpdateSuccess = new ArrayList<>();
         oracleDbNamedParameterJdbcTemplate.batchUpdate(successUpdateQuery, accountDataForBatchUpdateSuccess.toArray(new HashMap[successUpdateQuery.length()]));
    }
}
