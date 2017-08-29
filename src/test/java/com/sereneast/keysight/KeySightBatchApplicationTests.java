package com.sereneast.keysight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.OrchestraObject;
import com.sereneast.keysight.model.OrchestraObjectList;
import com.sereneast.keysight.util.ResultSetToOrachestraObjectRowMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KeySightBatchApplicationTests {

	@Resource
	private ApplicationProperties applicationProperties;

	@Resource
	private NamedParameterJdbcTemplate oracNamedParameterJdbcTemplate;

	@Resource
	private ResultSetToOrachestraObjectRowMapper resultSetToOrachestraObjectRowMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testObjectMapping() throws JsonProcessingException {
		List<OrchestraObject> resultList = oracNamedParameterJdbcTemplate.query("select * from mdm_account where interface_status = 'success' and system_id <'2000'",resultSetToOrachestraObjectRowMapper);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(resultList));
		OrchestraObjectList rows = new OrchestraObjectList();
		rows.setRows((List<OrchestraObject>)resultList);
		System.out.println(mapper.writeValueAsString(rows));
	}
}
