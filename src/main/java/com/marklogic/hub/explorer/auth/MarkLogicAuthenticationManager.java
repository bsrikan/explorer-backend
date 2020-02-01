/*
 * Copyright 2019 MarkLogic Corporation. All rights reserved.
 */
package com.marklogic.hub.explorer.auth;

import com.marklogic.client.DatabaseClient;
import com.marklogic.hub.explorer.util.DatabaseClientHolder;
import com.marklogic.hub.explorer.util.DbClientConfig;
import com.marklogic.hub.explorer.util.ExplorerConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Implements Spring Security's AuthenticationManager interface so that it can authenticate users by
 * making a simple request to MarkLogic and checking for a 401. Also implements
 * AuthenticationProvider so that it can be used with Spring Security's ProviderManager.
 */
@Component
public class MarkLogicAuthenticationManager implements AuthenticationProvider,
    AuthenticationManager {

  @Autowired
  DatabaseClientHolder databaseClientHolder;
  @Autowired
  ExplorerConfig explorerConfig;
  @Autowired
  DbClientConfig dbClientConfig;

  public MarkLogicAuthenticationManager() {
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ConnectionAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
    if (!(authentication instanceof ConnectionAuthenticationToken)) {
      throw new IllegalArgumentException(
          getClass().getName() + " only supports " + ConnectionAuthenticationToken.class
              .getName());
    }

    ConnectionAuthenticationToken token = (ConnectionAuthenticationToken) authentication;
    String username = token.getPrincipal().toString();
    String password = token.getCredentials().toString();

    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      throw new BadCredentialsException("Invalid credentials");
    }

    DatabaseClient databaseClient = dbClientConfig.createFinalDbClient(username, password);

    // Attempt connection
    DatabaseClient.ConnectionResult connectionResult;
    try {
      connectionResult = databaseClient.checkConnection();
    } catch (Exception e) {
      throw new InternalAuthenticationServiceException(e.getMessage());
    }

    if (connectionResult != null && !connectionResult.isConnected()) {
      if (connectionResult.getStatusCode() != null && connectionResult.getStatusCode() == 401) {
        throw new BadCredentialsException(connectionResult.getErrorMessage());
      } else {
        throw new InternalAuthenticationServiceException(connectionResult.getErrorMessage());
      }
    }

    // Now that we're authorized, store the databaseClient for future use in a session scoped bean.
    databaseClientHolder.setDatabaseClient(databaseClient);

    /*
     * Creating and storing a DatabaseClient object w/o explicitly specifying the database name as
     * data services only works with the default database associated with the App Server.
     */
    dbClientConfig.createFinalDbDataServiceClient(username, password);

    // Creating jobs database client
    dbClientConfig.createJobsDbClient(username, password);

    return new ConnectionAuthenticationToken(token.getPrincipal(), token.getCredentials(),
        token.getAuthorities());
  }
}

