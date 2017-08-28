package com.sereneast.keysight.model;

import java.util.List;

public class Account {

    private int articleId;

    private String title;

    private String category;

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private List<AccountAddress> accountAddresses;

    public List<AccountAddress> getAccountAddresses() {
        return accountAddresses;
    }

    public void setAccountAddresses(List<AccountAddress> accountAddresses) {
        this.accountAddresses = accountAddresses;
    }
}
