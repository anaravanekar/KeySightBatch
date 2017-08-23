package com.sereneast.keysight.rest.client;

//import javax.ws.rs.core.Response;

public class InvokeService {
/*
	public static String insertAccount(JsonObject accountDetails, String appHost,String appUser, String appPassword, String systemId, String systemName)
	{	
		// Call Orchestra Network Account Insert API
		 javax.ws.rs.client.Client c = ClientBuilder.newClient();
		 HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(appUser, appPassword);
		 c.register(feature);
		 String targetURL = "http://"+appHost+"/ebx-dataservices/rest/data/v1/BReference/Account/root/Account";
		 WebTarget target = c.target(targetURL);
	     Builder request = target.request();         	       
	     Response post = request.post(Entity.entity(accountDetails.toString(),MediaType.APPLICATION_JSON_TYPE));
	     String mdmId  = post.getHeaderString("location").substring(targetURL.length()+1,post.getHeaderString("location").length());
		 return (mdmId);
	}
	
	public static void insertAddress(JsonArray addressDetails, String appHost, String appUser, String appPassword)
	{
		// Call Orchestra Network Address Insert API
		 javax.ws.rs.client.Client c = ClientBuilder.newClient();
		 HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(appUser,appPassword);
		 c.register(feature);			
		 WebTarget target = c.target("http://"+appHost+"/ebx-dataservices/rest/data/v1/BReference/Account/root/Address");
	     Builder request = target.request();         	       
	     JsonObject input =  Json.createObjectBuilder()
	    		             .add("rows", addressDetails)
	    		             .build();     
	     request.post(Entity.entity(input.toString(),MediaType.APPLICATION_JSON_TYPE));
	}
	*/
}

