package com.marklogic.hub.explorer.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.hub.explorer.model.QueryDoc;
import com.marklogic.hub.explorer.model.QueryModel;
import com.marklogic.hub.explorer.util.DatabaseClientHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class QueryDocService {

  private static final String SEPARATOR_HYPHEN = "-";
  private static final String WRITE_COLL_SUFFIX = SEPARATOR_HYPHEN + "write";
  private static final String JSON_EXT = ".json";
  private static final String EXPLORER_URI = "/explorer/";
  private static final Logger logger = LoggerFactory.getLogger(QueryDocService.class);
  private static final DateTimeFormatter FORMATTER_TIME_ZONE = DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss Z");
  private static List<String> collections = new ArrayList<>(
      Arrays.asList("explorer", "explorer-query-doc"));
  @Autowired
  DatabaseClientHolder databaseClientHolder;

  public void saveQueryDoc(QueryModel queryModel) {
    // Initializing DatabaseClient and DocumentManager
    DatabaseClient dbClient = databaseClientHolder.getJobDbClient();
    GenericDocumentManager docMgr = dbClient.newDocumentManager();
    QueryDoc currDoc = queryModel.getQueryDoc();

    // Getting the user name
    String user = getCurrentUser();

    // Adding collections metadata on the document
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    collections.add(user + WRITE_COLL_SUFFIX);
    metadataHandle.getCollections().addAll(collections);

    // Assigning a docUri if its empty
    if (queryModel.getDocUri().isEmpty() || queryModel.getDocUri() == null) {
      String docUri = EXPLORER_URI + generateDocUri() + JSON_EXT;
      queryModel.setDocUri(docUri);
      logger.debug(docUri);
    }

    // Create queryId for the doc. The doc uri doesn't have a meaning once it leaves out of the
    // database. If the query has to be shared to a different db, the doc should identify itself
    currDoc.setQueryId(currDoc.getQueryName() + SEPARATOR_HYPHEN + System.currentTimeMillis());

    // Setting the document updated time to current time in UTC in string format
    ZonedDateTime currTime = ZonedDateTime.now(ZoneOffset.UTC);
    String docUpdateTime = currTime.format(FORMATTER_TIME_ZONE);
    currDoc.setDocUpdateTime(docUpdateTime);

    // Serializing QueryDoc object to Json Node
    ObjectMapper mapper = new ObjectMapper();
    JsonNode queryDocNode = mapper.valueToTree(currDoc);

    // Writing the document into database in Json format
    docMgr.writeAs(queryModel.getDocUri(), metadataHandle, queryDocNode);
  }

  private String generateDocUri() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }

  private String getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.getPrincipal().toString();
  }
}
