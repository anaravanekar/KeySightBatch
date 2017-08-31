package com.sereneast.keysight.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import com.sereneast.keysight.model.OrchestraObjectList;
import com.sereneast.keysight.model.OrchestraObjectListResponse;
import com.sereneast.keysight.model.OrchestraResponseDetails;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Service
public class OrchestraRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestraRestClient.class);

    private ApplicationProperties applicationProperties;

    private String baseUrl;

    private HttpAuthenticationFeature feature;

    @Autowired
    public OrchestraRestClient(ApplicationProperties applicationProperties) {
        Map<String,String> restProperties = applicationProperties.getOrchestraRest();
        StringBuilder base = new StringBuilder();
        if("true".equalsIgnoreCase(restProperties.get("ssl"))){
            base.append("https://");
        }else{
            base.append("http://");
        }
        base.append(restProperties.get("host"));
        base.append(":"+restProperties.get("port"));
        base.append(restProperties.get("baseURI"));
        base.append(restProperties.get("version"));
        this.baseUrl = base.toString();
        this.feature = HttpAuthenticationFeature.basic(restProperties.get("username"), restProperties.get("password"));
    }


    public OrchestraObjectListResponse get(final String dataSpace,final String dataSet,final String path,final Map<String,String> parameters) throws IOException {
        Client client = ClientBuilder.newClient();
        try {
            client.register(feature);
            WebTarget target = client.target(baseUrl).path(dataSpace).path(dataSet).path(path);
            if (parameters != null)
                for (Map.Entry<String, String> entry : parameters.entrySet())
                    target = target.queryParam(entry.getKey(), entry.getValue());
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();

            LOGGER.debug(String.valueOf(response.getStatus()));
            LOGGER.debug(response.getStatusInfo().toString());

            if (response.getStatus() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                OrchestraObjectListResponse responseJson = mapper.readValue(response.readEntity(String.class), OrchestraObjectListResponse.class);
                LOGGER.debug(mapper.writeValueAsString(responseJson));
                return responseJson;
            }
        }finally{
            client.close();
        }
        return null;
    }

    public OrchestraResponseDetails insert(final String dataSpace, final String dataSet, final String path, OrchestraObjectList requestObject, final Map<String,String> parameters) throws IOException {
        Client client = ClientBuilder.newClient();
        ObjectMapper mapper = new ObjectMapper();
        try {
            client.register(feature);
            WebTarget target = client.target(baseUrl).path(dataSpace).path(dataSet).path(path);
            if (parameters != null)
                for (Map.Entry<String, String> entry : parameters.entrySet())
                    target = target.queryParam(entry.getKey(), entry.getValue());
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.post(Entity.json(mapper.writeValueAsString(requestObject)));

            LOGGER.debug(String.valueOf(response.getStatus()));
            LOGGER.debug(response.getStatusInfo().toString());

            if (response.getStatus() == 200) {
                OrchestraResponseDetails responseJson = mapper.readValue(response.readEntity(String.class), OrchestraResponseDetails.class);
                LOGGER.debug(mapper.writeValueAsString(responseJson));
                return responseJson;
            }else{
                throw new RuntimeException("Error inserting records");
            }
        }finally{
            client.close();
        }
    }
}

