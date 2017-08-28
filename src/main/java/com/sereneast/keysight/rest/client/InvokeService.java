package com.sereneast.keysight.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sereneast.keysight.model.OrchestraObject;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
public class InvokeService {

	public String insertAccount(OrchestraObject accountDetails, String appHost, String appUser, String appPassword, String systemId, String systemName) throws JsonProcessingException {
		// Call Orchestra Network Account Insert API
        javax.ws.rs.client.Client c = ClientBuilder.newClient();
		 HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(appUser, appPassword);
		 c.register(feature);
		 String targetURL = "http://"+appHost+"/ebx-dataservices/rest/data/v1/BReference/Account/root/Account";
		 WebTarget target = c.target(targetURL);
	     Invocation.Builder request = target.request();
        ObjectMapper mapper = new ObjectMapper();
        Response post = request.post(Entity.entity(mapper.writeValueAsString(accountDetails), MediaType.APPLICATION_JSON_TYPE));
	     String mdmId  = post.getHeaderString("location").substring(targetURL.length()+1,post.getHeaderString("location").length());
		 return (mdmId);
	}
	
	/*public static void insertAddress(JsonArray addressDetails, String appHost, String appUser, String appPassword)
	{
		// Call Orchestra Network Address Insert API
		 javax.ws.rs.client.Client c = ClientBuilder.newClient();
		 HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(appUser,appPassword);
		 c.register(feature);			
		 WebTarget target = c.target("http://"+appHost+"/ebx-dataservices/rest/data/v1/BReference/Account/root/Address");
	     Invocation.Builder request = target.request();
	     JsonObject input =  Json.createObjectBuilder()
	    		             .add("rows", addressDetails)
	    		             .build();     
	     request.post(Entity.entity(input.toString(),MediaType.APPLICATION_JSON_TYPE));
	}
	*/
}

