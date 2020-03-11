package com.diego.twittetbot.services;

import com.diego.twittetbot.entities.WebSiteInfo;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TwitterServiceImpl implements ITwitterService {

    private static final String COUNTRY_LOCATION = "Country";
    private static final Set<String> COUNTRY_CODES = ImmutableSet.of("MX", "US", "AR", "CL", "CA", "CO", "EC", "PE",
            "PA", "PR", "ES", "VE");
    private static final String STATUS_STRING = "Ayuda a %s con su tarea entrando a %s por favor";
    private static final String APPEND_FORMAT = "%s %s";

    @Override
    public void twittAskingForTraffic(List<WebSiteInfo> webSitesInfo) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        System.out.println("Trends");
        Set <String> hashTags = twitter.trends().getAvailableTrends().stream()
                .filter(location -> COUNTRY_LOCATION.equals(location.getPlaceName()))
                .filter(location -> COUNTRY_CODES.contains(location.getCountryCode()))
                .map(Location::getWoeid)
                .map(this::getTrendingTopics)
                .flatMap(trendings -> trendings.stream()
                        .filter(trendingTopic -> trendingTopic.startsWith("#"))
                        .limit(3))
                .collect(Collectors.toSet());
        for (WebSiteInfo webSiteInfo : webSitesInfo) {
            twit(webSiteInfo, hashTags);
        }
    }

    private Set<String> getTrendingTopics(Integer placeId) {
        try {
            return Arrays.stream(TwitterFactory.getSingleton().trends().getPlaceTrends(placeId).getTrends())
                    .map(Trend::getName)
                    .collect(Collectors.toSet());
        } catch (TwitterException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    private void twit(WebSiteInfo webSiteInfo, Set<String> hashtags) {
        final String originalStatus = String.format(STATUS_STRING, webSiteInfo.getTwitterAccount(), webSiteInfo.getUrl());
        String newStatus = originalStatus;
        System.out.printf("---> Twitting %s\n", newStatus);
        for (String hashTag : hashtags) {
            String appendHashTagStatus = String.format(APPEND_FORMAT, newStatus, hashTag);
            if (appendHashTagStatus.length() > 140) {
                updateStatus(newStatus);
                appendHashTagStatus = String.format(APPEND_FORMAT, originalStatus, hashTag);
            }
            newStatus = appendHashTagStatus;
        }
        updateStatus(newStatus);
    }

    private void updateStatus(String newStatus) {
        Twitter twitter = TwitterFactory.getSingleton();
        System.out.printf("Trying to tweet (%d) %s\n", newStatus.length(), newStatus);
        try {
            //A twit every 6 minutes
            twitter.updateStatus(newStatus);
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
