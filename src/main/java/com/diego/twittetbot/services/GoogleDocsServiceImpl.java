package com.diego.twittetbot.services;

import com.diego.twittetbot.entities.WebSiteInfo;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleDocsServiceImpl implements IGoogleDocsService {

    @Value("${application.name}")
    private String applicationName;

    @Value("${google.spreadsheet.credentials.file}")
    private String credentialsJsonFile;

    @Value("${google.spreadsheet.id}")
    private String spreadSheetId;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String RANGE = "A2:C";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public List<WebSiteInfo> getWebSitesInfo() throws GeneralSecurityException, IOException {
        System.out.println("Getting web sites information from google docs.");
        System.out.printf("Reading spreadsheet: %s\n", spreadSheetId);
        final NetHttpTransport NET_HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(NET_HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(applicationName)
                .build();
        return service.spreadsheets().values()
                .get(spreadSheetId, RANGE)
                .execute()
                .getValues()
                .stream()
                .map(this::createWebSiteInfo)
                .collect(Collectors.toList());
    }

    private HttpRequestInitializer getCredentials() throws IOException, GeneralSecurityException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(credentialsJsonFile));
         final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private WebSiteInfo createWebSiteInfo(List<Object> rows) {
        return new WebSiteInfo()
                .setName(rows.get(0).toString())
                .setTwitterAccount(rows.get(1).toString())
                .setUrl(rows.get(2).toString());
    }
}
