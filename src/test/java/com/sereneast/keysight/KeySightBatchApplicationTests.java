package com.sereneast.keysight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.*;
import com.sereneast.keysight.rest.client.OrchestraRestClient;
import com.sereneast.keysight.util.ResultSetToOrachestraObjectRowMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
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

	@Resource
	private OrchestraRestClient orchestraRestClient;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testObjectMapping() throws IOException {
		List<OrchestraObject> resultList = oracNamedParameterJdbcTemplate.query("select * from mdm_account where interface_status = 'success' and system_id <'2000'",resultSetToOrachestraObjectRowMapper);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(resultList));
		OrchestraObjectList rows = new OrchestraObjectList();
		rows.setRows((List<OrchestraObject>)resultList);
		System.out.println(mapper.writeValueAsString(rows));

		List<OrchestraDetails> detailsList = new ArrayList<>();
		OrchestraDetails d1 = new OrchestraDetails();
		d1.setDetails("http://10.10.10.90:8080/ebx-dataservices/rest/data/v1/BReference/Account/root/Account/8");
		detailsList.add(d1);
		d1 = new OrchestraDetails();
		d1.setDetails("http://10.10.10.90:8080/ebx-dataservices/rest/data/v1/BReference/Account/root/Account/8");
		detailsList.add(d1);
		OrchestraResponseDetails response = new OrchestraResponseDetails();
		response.setRows(detailsList);
		System.out.println(mapper.writeValueAsString(response));

		OrchestraObjectListResponse resp = orchestraRestClient.get("BReference","Account","root/Account",null);
		System.out.println(mapper.writeValueAsString(resp));

	}
}
