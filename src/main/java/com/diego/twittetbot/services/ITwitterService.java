package com.diego.twittetbot.services;

import com.diego.twittetbot.entities.WebSiteInfo;
import twitter4j.TwitterException;

import java.util.List;

public interface ITwitterService {

    void twittAskingForTraffic(List<WebSiteInfo> webSitesInfo) throws TwitterException;
}
