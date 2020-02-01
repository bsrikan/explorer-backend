package com.marklogic.hub.explorer.model;

public class QueryDoc {

  private String queryId;
  private String queryName;
  private String queryDescription;
  private String docUpdateTime;
  private DocSearchQueryInfo jsonQuery;

  public QueryDoc() {
    this.queryId = "";
    this.queryDescription = "";
    this.docUpdateTime = "";
  }

  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }

  public String getQueryDescription() {
    return queryDescription;
  }

  public void setQueryDescription(String queryDescription) {
    this.queryDescription = queryDescription;
  }

  public String getDocUpdateTime() {
    return docUpdateTime;
  }

  public void setDocUpdateTime(String docUpdateTime) {
    this.docUpdateTime = docUpdateTime;
  }

  public DocSearchQueryInfo getJsonQuery() {
    return jsonQuery;
  }

  public void setJsonQuery(DocSearchQueryInfo jsonQuery) {
    this.jsonQuery = jsonQuery;
  }
}
