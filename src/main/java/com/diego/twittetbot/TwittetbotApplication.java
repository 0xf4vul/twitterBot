package com.diego.twittetbot;

import com.diego.twittetbot.services.IGoogleDocsService;
import com.diego.twittetbot.services.ITwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwittetbotApplication implements CommandLineRunner {

	@Autowired
	IGoogleDocsService googleDocsService;

	@Autowired
	ITwitterService twitterService;

	public static void main(String[] args) {
		SpringApplication.run(TwittetbotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Executing twitter bot.");
		twitterService.twittAskingForTraffic(googleDocsService.getWebSitesInfo());
		System.out.println("Done!");
	}
}
