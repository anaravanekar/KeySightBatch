package com.sereneast.keysight.batch.writer;

import com.sereneast.keysight.model.OrchestraObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class AddressRestWriter implements ItemWriter<OrchestraObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressRestWriter.class);

    @Autowired
    private NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate;

    private static final String successUpdateQuery =
            "UPDATE mdm_account SET interface_status='success', execution_date=:executionDate"+
                    " mdm_account_id=:mdmAccountId  where system_id=:systemId AND system_name=:systemName";

    private static final String errorUpdateQuery = "";

    @Override
    public void write(List<? extends OrchestraObject> addresses) throws Exception {
        LOGGER.debug("Writing addresses "+addresses.size());
    }
}
