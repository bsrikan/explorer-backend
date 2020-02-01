package com.marklogic.hub.explorer.util;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.DefaultConfiguredDatabaseClientFactory;
import com.marklogic.client.ext.SecurityContextType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbClientConfig {

  @Autowired
  ExplorerConfig explorerConfig;
  @Autowired
  DatabaseClientHolder databaseClientHolder;

  public DatabaseClient createFinalDbClient(String username, String password) {
    DatabaseClientConfig finalDbClientConfig = getFinalDbClientConfig(username, password);
    return createDbClient(finalDbClientConfig);
  }

  /*
   * Creating and storing a DatabaseClient object w/o explicitly specifying the database name as
   * data services only works with the default database associated with the App Server.
   */
  public void createFinalDbDataServiceClient(String username, String password) {
    DatabaseClientConfig dataServiceClientConfig = getFinalDbClientConfig(username, password);
    dataServiceClientConfig.setDatabase(null);
    databaseClientHolder.setDataServiceClient(createDbClient(dataServiceClientConfig));
  }

  public void createJobsDbClient(String username, String password) {
    DatabaseClientConfig jobDbClientConfig = getJobDbClientConfig(username, password);
    databaseClientHolder.setJobDbClient(createDbClient(jobDbClientConfig));
  }

  private DatabaseClientConfig getFinalDbClientConfig(String username, String password) {
    DatabaseClientConfig clientConfig = new DatabaseClientConfig(explorerConfig.getHostname(),
        explorerConfig.getFinalPort(), username, password);
    clientConfig.setDatabase(explorerConfig.getFinalDbName());
    clientConfig.setSecurityContextType(SecurityContextType.valueOf(
        explorerConfig.getFinalAuthMethod().toUpperCase()));
    clientConfig.setSslHostnameVerifier(explorerConfig.getFinalSslHostnameVerifier());
    clientConfig.setSslContext(explorerConfig.getFinalSslContext());
    clientConfig.setCertFile(explorerConfig.getFinalCertFile());
    clientConfig.setCertPassword(explorerConfig.getFinalCertPassword());
    clientConfig.setExternalName(explorerConfig.getFinalExternalName());
    clientConfig.setTrustManager(explorerConfig.getFinalTrustManager());
    if (explorerConfig.getHostLoadBalancer()) {
      clientConfig.setConnectionType(DatabaseClient.ConnectionType.GATEWAY);
    }
    return clientConfig;
  }

  private DatabaseClientConfig getJobDbClientConfig(String username, String password) {
    DatabaseClientConfig clientConfig = new DatabaseClientConfig(explorerConfig.getHostname(),
        explorerConfig.getJobPort(), username, password);
    clientConfig.setDatabase(explorerConfig.getJobDbName());
    clientConfig.setSecurityContextType(SecurityContextType.valueOf(
        explorerConfig.getJobAuthMethod().toUpperCase()));
    clientConfig.setSslHostnameVerifier(explorerConfig.getJobSslHostnameVerifier());
    clientConfig.setSslContext(explorerConfig.getJobSslContext());
    clientConfig.setCertFile(explorerConfig.getJobCertFile());
    clientConfig.setCertPassword(explorerConfig.getJobCertPassword());
    clientConfig.setExternalName(explorerConfig.getJobExternalName());
    clientConfig.setTrustManager(explorerConfig.getJobTrustManager());
    if (explorerConfig.getHostLoadBalancer()) {
      clientConfig.setConnectionType(DatabaseClient.ConnectionType.GATEWAY);
    }
    return clientConfig;
  }

  private DatabaseClient createDbClient(DatabaseClientConfig config) {
    return new DefaultConfiguredDatabaseClientFactory().newDatabaseClient(config);
  }
}
