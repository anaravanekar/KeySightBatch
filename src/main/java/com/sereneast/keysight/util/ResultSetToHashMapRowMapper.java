package com.sereneast.keysight.util;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultSetToHashMapRowMapper implements RowMapper<Map<String,Object>> {

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String,Object> item = new LinkedHashMap<String, Object>();
		ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount();
		for (int i = 1; i <= count; i++) {
		   item.put(metaData.getColumnLabel(i), rs.getObject(metaData.getColumnLabel(i))); 
		}
		return item;
	}
}