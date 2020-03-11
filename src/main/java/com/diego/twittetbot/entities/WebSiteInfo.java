package com.diego.twittetbot.entities;

public class WebSiteInfo {

    private String name;
    private String twitterAccount;
    private String url;

    public String getName() {
        return name;
    }

    public WebSiteInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public WebSiteInfo setTwitterAccount(String twitterAccount) {
        this.twitterAccount = twitterAccount;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WebSiteInfo setUrl(String url) {
        this.url = url;
        return this;
    }
}
