package com.diego.twittetbot.services;

import com.diego.twittetbot.entities.WebSiteInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface IGoogleDocsService {
    List<WebSiteInfo> getWebSitesInfo() throws GeneralSecurityException, IOException;
}
