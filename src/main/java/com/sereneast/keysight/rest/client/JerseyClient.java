package com.sereneast.keysight.rest.client;

import com.sereneast.keysight.model.Account;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class JerseyClient {
	public void getArticleDetails() {
		Client client = ClientBuilder.newClient();
		WebTarget base = client.target("http://localhost:8033/spring-app/article");
		WebTarget details = base.path("details");
		List<Account> list = details.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Account>>() {});

		list.stream().forEach(article ->
				System.out.println(article.getArticleId()+", "+ article.getTitle()+", "+ article.getCategory()));

		client.close();
	}
	public void getArticleById(int articleId) {
		Client client = ClientBuilder.newClient();
		WebTarget base = client.target("http://localhost:8033/spring-app/article");
		WebTarget articleById = base.path("{id}").resolveTemplate("id", articleId);
		Account article = articleById.request(MediaType.APPLICATION_JSON)
				.get(Account.class);

		System.out.println(article.getArticleId()+", "+ article.getTitle()+", "+ article.getCategory());

		client.close();
	}
	public void addAccount(Account article) {
		Client client = ClientBuilder.newClient();
		WebTarget base = client.target("http://localhost:8033/spring-app/article");
		WebTarget add = base.path("add");
		Response response = add.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(article));

		System.out.println("Response Http Status: "+ response.getStatus());
		System.out.println(response.getLocation());

		client.close();
	}
	public void updateAccount(Account article) {
		Client client = ClientBuilder.newClient();
		WebTarget base = client.target("http://localhost:8033/spring-app/article");
		WebTarget update = base.path("update");
		Response response = update.request(MediaType.APPLICATION_JSON)
				.put(Entity.json(article));

		System.out.println("Response Http Status: "+ response.getStatus());
		Account resAccount = response.readEntity(Account.class);
		System.out.println(resAccount.getArticleId()+", "+ resAccount.getTitle()+", "+ resAccount.getCategory());

		client.close();
	}
	public void deleteAccount(int articleId) {
		Client client = ClientBuilder.newClient();
		WebTarget base = client.target("http://localhost:8033/spring-app/article");
		WebTarget deleteById = base.path("{id}").resolveTemplate("id", articleId);
		Response response = deleteById.request(MediaType.APPLICATION_JSON)
				.delete();

		System.out.println("Response Http Status: "+ response.getStatus());
		if(response.getStatus() == 204) {
			System.out.println("Data deleted successfully.");
		}

		client.close();
	}
	public static void main(String[] args) {
		JerseyClient jerseyClient = new JerseyClient();
		jerseyClient.getArticleDetails();
		//jerseyClient.getArticleById(102);

		Account article = new Account();
		article.setTitle("Spring REST Security using Hibernate2");
		article.setCategory("Spring");
		//jerseyClient.addAccount(article);

		article.setArticleId(105);
		//jerseyClient.updateAccount(article);

		//jerseyClient.deleteAccount(105);
	}
} 