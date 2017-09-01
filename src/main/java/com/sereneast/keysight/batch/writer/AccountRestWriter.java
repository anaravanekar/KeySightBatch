package com.sereneast.keysight.batch.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.model.OrchestraObjectList;
import com.sereneast.keysight.model.OrchestraResponseDetails;
import com.sereneast.keysight.rest.client.OrchestraRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.util.*;

public class AccountRestWriter implements ItemWriter<com.sereneast.keysight.model.OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRestWriter.class);

    private static final String DATA_SPACE = "BReference";

    private static final String DATA_SET = "Account";

    private static final String PATH = "root/Account";

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    @Resource
    private OrchestraRestClient orchestraRestClient;

    private static final String SUCCESS_QUERY =
            "UPDATE mdm_account SET interface_status='success',"+
                    " mdm_account_id=:mdmAccountId  where system_id=:systemId";

    private static final String SUCCESS_QUERY_ADDRESS =
            "UPDATE mdm_account_address SET "+
                    " mdm_account_id=:mdmAccountId  where system_id=:systemId";

    private static final String FAILED_QUERY =
            "UPDATE mdm_account SET interface_status='failed'"+
                    " where system_id=:systemId";

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends OrchestraObject> accounts){
        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String,Object>> accountDataForBatchUpdateSuccess = new ArrayList<>();
        OrchestraObjectList rows = new OrchestraObjectList();
        rows.setRows((List<OrchestraObject>)accounts);

        for(OrchestraObject account: accounts){
            LinkedHashMap<String,Object> batchParams = new LinkedHashMap<>();
            batchParams.put("systemId",account.getContent().get("SystemId").getContent());
            accountDataForBatchUpdateSuccess.add(batchParams);
            LOGGER.debug(Thread.currentThread().getName() + " Writing account " + account.getContent().get("SystemId").getContent());
        }

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("includeDetails","true");
        OrchestraResponseDetails resp = null;
        try {
            resp = orchestraRestClient.insert(DATA_SPACE, DATA_SET, PATH, rows, parameters);
        }catch (Exception e){
            LOGGER.error("Error inserting accounts ",e);
        }

        if(resp!=null) {
            for (int i = 0; i < resp.getRows().size(); i++) {
                for (int j = 0; j < accountDataForBatchUpdateSuccess.size(); j++) {
                    if (i == j) {
                        String details = resp.getRows().get(i).getDetails();
                        accountDataForBatchUpdateSuccess.get(j).put("mdmAccountId", details.substring(details.lastIndexOf('/') + 1, details.length()));
                    }
                }
            }
            LinkedHashMap<String,Object>[] map = new LinkedHashMap[accountDataForBatchUpdateSuccess.size()];
            oracleDbNamedParameterJdbcTemplate.batchUpdate(SUCCESS_QUERY,accountDataForBatchUpdateSuccess.toArray(map));
            oracleDbNamedParameterJdbcTemplate.batchUpdate(SUCCESS_QUERY_ADDRESS,accountDataForBatchUpdateSuccess.toArray(map));
        }else{
            LinkedHashMap<String,Object>[] map = new LinkedHashMap[accountDataForBatchUpdateSuccess.size()];
            oracleDbNamedParameterJdbcTemplate.batchUpdate(FAILED_QUERY,accountDataForBatchUpdateSuccess.toArray(map));
        }
    }
}
