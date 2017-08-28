package com.sereneast.keysight.util;

import com.sereneast.keysight.model.OrchestraContent;
import com.sereneast.keysight.model.OrchestraObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ResultSetToOrachestraObjectRowMapper implements RowMapper<OrchestraObject> {

    @Override
    public OrchestraObject mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrchestraObject orchestraObject = new OrchestraObject();
        Map<String,OrchestraContent> fields = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            fields.put(metaData.getColumnLabel(i), new OrchestraContent(rs.getObject(metaData.getColumnLabel(i))));
        }
        orchestraObject.setContent(fields);
        return orchestraObject;
    }
}
