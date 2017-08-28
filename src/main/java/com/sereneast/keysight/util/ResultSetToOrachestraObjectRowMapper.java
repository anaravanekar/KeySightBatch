package com.sereneast.keysight.util;

import com.sereneast.keysight.config.properties.AccountJobProperties;
import com.sereneast.keysight.model.OrchestraContent;
import com.sereneast.keysight.model.OrchestraObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ResultSetToOrachestraObjectRowMapper implements RowMapper<OrchestraObject> {

    @Autowired
    private AccountJobProperties accountJobProperties;

    @Override
    public OrchestraObject mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrchestraObject orchestraObject = new OrchestraObject();
        Map<String,OrchestraContent> fields = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            fields.put(accountJobProperties.getMapping().get(metaData.getColumnLabel(i).toLowerCase()), new OrchestraContent(rs.getObject(metaData.getColumnLabel(i))));
        }
        orchestraObject.setContent(fields);
        return orchestraObject;
    }
}
