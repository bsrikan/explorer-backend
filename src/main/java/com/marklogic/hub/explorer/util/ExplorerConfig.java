/*
 * Copyright 2019 MarkLogic Corporation. All rights reserved.
 */
package com.marklogic.hub.explorer.util;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:explorer-defaults.properties"})
public class ExplorerConfig {

  private static final String QUERIES_FILE = "SqlQueries.properties";
  private static final Logger logger = LoggerFactory.getLogger(ExplorerConfig.class);
  private Properties queryProperties = null;

  @Value("${mlHost}")
  private String hostname;
  @Value("${mlIsHostLoadBalancer}")
  private Boolean isHostLoadBalancer;

  // Final database properties
  @Value("${mlFinalDbName:#{null}}")
  private String finalDbName;
  @Value("${mlFinalPort}")
  private Integer finalPort;
  @Value("${mlFinalAuth}")
  private String finalAuthMethod;
  @Value("${mlFinalScheme}")
  private String finalScheme;
  @Value("${mlFinalSimpleSsl}")
  private Boolean finalSimpleSsl;
  private SSLContext finalSslContext;
  private DatabaseClientFactory.SSLHostnameVerifier finalSslHostnameVerifier;
  private X509TrustManager finalTrustManager;
  @Value("${mlFinalCertFile:#{null}}")
  private String finalCertFile;
  @Value("${mlFinalCertPassword:#{null}}")
  private String finalCertPassword;
  @Value("${mlFinalExternalName:#{null}}")
  private String finalExternalName;

  // Job database properties
  @Value("${mlJobDbName:#{null}}")
  private String jobDbName;
  @Value("${mlJobPort}")
  private Integer jobPort;
  @Value("${mlJobAuth}")
  private String jobAuthMethod;
  @Value("${mlJobScheme}")
  private String jobScheme;
  @Value("${mlJobSimpleSsl}")
  private Boolean jobSimpleSsl;
  private SSLContext jobSslContext;
  private DatabaseClientFactory.SSLHostnameVerifier jobSslHostnameVerifier;
  private X509TrustManager jobTrustManager;
  @Value("${mlJobCertFile:#{null}}")
  private String jobCertFile;
  @Value("${mlJobCertPassword:#{null}}")
  private String jobCertPassword;
  @Value("${mlJobExternalName:#{null}}")
  private String jobExternalName;

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public Boolean getHostLoadBalancer() {
    return isHostLoadBalancer;
  }

  public void setHostLoadBalancer(Boolean hostLoadBalancer) {
    isHostLoadBalancer = hostLoadBalancer;
  }

  public String getFinalDbName() {
    return finalDbName;
  }

  public void setFinalDbName(String finalDbName) {
    this.finalDbName = finalDbName;
  }

  public Integer getFinalPort() {
    return finalPort;
  }

  public void setFinalPort(Integer finalPort) {
    this.finalPort = finalPort;
  }

  public String getFinalAuthMethod() {
    return finalAuthMethod;
  }

  public void setFinalAuthMethod(String finalAuthMethod) {
    this.finalAuthMethod = finalAuthMethod;
  }

  public String getFinalScheme() {
    return finalScheme;
  }

  public void setFinalScheme(String finalScheme) {
    this.finalScheme = finalScheme;
  }

  public Boolean getFinalSimpleSsl() {
    return finalSimpleSsl;
  }

  public void setFinalSimpleSsl(Boolean finalSimpleSsl) {
    this.finalSimpleSsl = finalSimpleSsl;
  }

  public SSLContext getFinalSslContext() {
    return finalSslContext;
  }

  public void setFinalSslContext(SSLContext finalSslContext) {
    this.finalSslContext = finalSslContext;
  }

  public DatabaseClientFactory.SSLHostnameVerifier getFinalSslHostnameVerifier() {
    return finalSslHostnameVerifier;
  }

  public void setFinalSslHostnameVerifier(
      DatabaseClientFactory.SSLHostnameVerifier finalSslHostnameVerifier) {
    this.finalSslHostnameVerifier = finalSslHostnameVerifier;
  }

  public String getFinalCertFile() {
    return finalCertFile;
  }

  public void setFinalCertFile(String finalCertFile) {
    this.finalCertFile = finalCertFile;
  }

  public String getFinalCertPassword() {
    return finalCertPassword;
  }

  public void setFinalCertPassword(String finalCertPassword) {
    this.finalCertPassword = finalCertPassword;
  }

  public String getFinalExternalName() {
    return finalExternalName;
  }

  public void setFinalExternalName(String finalExternalName) {
    this.finalExternalName = finalExternalName;
  }

  public X509TrustManager getFinalTrustManager() {
    return finalTrustManager;
  }

  public void setFinalTrustManager(X509TrustManager finalTrustManager) {
    this.finalTrustManager = finalTrustManager;
  }

  public String getJobDbName() {
    return jobDbName;
  }

  public void setJobDbName(String jobDbName) {
    this.jobDbName = jobDbName;
  }

  public Integer getJobPort() {
    return jobPort;
  }

  public void setJobPort(Integer jobPort) {
    this.jobPort = jobPort;
  }

  public String getJobAuthMethod() {
    return jobAuthMethod;
  }

  public void setJobAuthMethod(String jobAuthMethod) {
    this.jobAuthMethod = jobAuthMethod;
  }

  public String getJobScheme() {
    return jobScheme;
  }

  public void setJobScheme(String jobScheme) {
    this.jobScheme = jobScheme;
  }

  public Boolean getJobSimpleSsl() {
    return jobSimpleSsl;
  }

  public void setJobSimpleSsl(Boolean jobSimpleSsl) {
    this.jobSimpleSsl = jobSimpleSsl;
  }

  public SSLContext getJobSslContext() {
    return jobSslContext;
  }

  public void setJobSslContext(SSLContext jobSslContext) {
    this.jobSslContext = jobSslContext;
  }

  public DatabaseClientFactory.SSLHostnameVerifier getJobSslHostnameVerifier() {
    return jobSslHostnameVerifier;
  }

  public void setJobSslHostnameVerifier(
      DatabaseClientFactory.SSLHostnameVerifier jobSslHostnameVerifier) {
    this.jobSslHostnameVerifier = jobSslHostnameVerifier;
  }

  public X509TrustManager getJobTrustManager() {
    return jobTrustManager;
  }

  public void setJobTrustManager(X509TrustManager jobTrustManager) {
    this.jobTrustManager = jobTrustManager;
  }

  public String getJobCertFile() {
    return jobCertFile;
  }

  public void setJobCertFile(String jobCertFile) {
    this.jobCertFile = jobCertFile;
  }

  public String getJobCertPassword() {
    return jobCertPassword;
  }

  public void setJobCertPassword(String jobCertPassword) {
    this.jobCertPassword = jobCertPassword;
  }

  public String getJobExternalName() {
    return jobExternalName;
  }

  public void setJobExternalName(String jobExternalName) {
    this.jobExternalName = jobExternalName;
  }

  public Properties getQueryProperties() {
    return this.queryProperties;
  }

  @PostConstruct
  private void simpleSslSetup() {
    if (BooleanUtils.isTrue(finalSimpleSsl)) {
      finalSslContext = SimpleX509TrustManager.newSSLContext();
      finalSslHostnameVerifier = DatabaseClientFactory.SSLHostnameVerifier.ANY;
      finalTrustManager = new SimpleX509TrustManager();
    }

    if (BooleanUtils.isTrue(jobSimpleSsl)) {
      jobSslContext = SimpleX509TrustManager.newSSLContext();
      jobSslHostnameVerifier = DatabaseClientFactory.SSLHostnameVerifier.ANY;
      jobTrustManager = new SimpleX509TrustManager();
    }
  }

  @PostConstruct
  public void getQueries() {
    try {
      this.queryProperties = ExplorerUtil.getPropertiesFromClassPath(QUERIES_FILE);
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }
}