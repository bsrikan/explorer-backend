package com.marklogic.hub.explorer.model;

public class QueryModel {

  private String docUri;
  private QueryDoc queryDoc;

  public QueryModel() {
    this.docUri = "";
    this.queryDoc = new QueryDoc();
  }

  public String getDocUri() {
    return docUri;
  }

  public void setDocUri(String docUri) {
    this.docUri = docUri;
  }

  public QueryDoc getQueryDoc() {
    return queryDoc;
  }

  public void setQueryDoc(QueryDoc queryDoc) {
    this.queryDoc = queryDoc;
  }
}
