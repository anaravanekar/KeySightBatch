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

public class AddressRestWriter implements ItemWriter<OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressRestWriter.class);

    private static final String DATA_SPACE = "BReference";

    private static final String DATA_SET = "Account";

    private static final String PATH = "root/Address";

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    @Resource
    private OrchestraRestClient orchestraRestClient;

    private static final String SUCCESS_QUERY =
            "UPDATE mdm_account_address SET interface_status='success',"+
                    " mdm_address_id=:mdmAddressId  where ID=:id";

    private static final String FAILED_QUERY =
            "UPDATE mdm_account_address SET interface_status='failed'"+
                    " where ID=:id";
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends OrchestraObject> addresses) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String,Object>> addressDataForBatchUpdateSuccess = new ArrayList<>();
        OrchestraObjectList rows = new OrchestraObjectList();
        rows.setRows((List<OrchestraObject>)addresses);

        for(OrchestraObject address: addresses){
            LinkedHashMap<String,Object> batchParams = new LinkedHashMap<>();
            batchParams.put("id",address.getContent().get("id").getContent());
            addressDataForBatchUpdateSuccess.add(batchParams);
            LOGGER.debug(Thread.currentThread().getName() + " Writing address " + address.getContent().get("id").getContent());
        }

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("includeDetails","true");
        OrchestraResponseDetails resp = null;
        try {
            resp = orchestraRestClient.insert(DATA_SPACE, DATA_SET, PATH, rows, parameters);
        }catch (Exception e){
            LOGGER.error("Error inserting addresses ",e);
        }

        if(resp!=null) {
            for (int i = 0; i < resp.getRows().size(); i++) {
                for (int j = 0; j < addressDataForBatchUpdateSuccess.size(); j++) {
                    if (i == j) {
                        String details = resp.getRows().get(i).getDetails();
                        addressDataForBatchUpdateSuccess.get(j).put("mdmAddressId", details.substring(details.lastIndexOf('/') + 1, details.length()));
                    }
                }
            }
            LinkedHashMap<String,Object>[] map = new LinkedHashMap[addressDataForBatchUpdateSuccess.size()];
            oracleDbNamedParameterJdbcTemplate.batchUpdate(SUCCESS_QUERY,addressDataForBatchUpdateSuccess.toArray(map));
        }else{
            LinkedHashMap<String,Object>[] map = new LinkedHashMap[addressDataForBatchUpdateSuccess.size()];
            oracleDbNamedParameterJdbcTemplate.batchUpdate(FAILED_QUERY,addressDataForBatchUpdateSuccess.toArray(map));
        }
    }
}
