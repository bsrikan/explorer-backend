package com.marklogic.hub.explorer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.hub.explorer.model.FacetInfo;
import com.marklogic.hub.explorer.model.FacetSearchQuery;
import com.marklogic.hub.explorer.util.DatabaseClientHolder;
import com.marklogic.hub.explorer.util.ExplorerConfig;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FacetSearchService {

  public static final String ANY_STRING = "%";
  public static final String ANY_CHAR = "_";
  public static final String DOT_CHAR = ".";
  public static final String STRING_IDENTIFIER = "'";
  public static final String STRING_DATATYPE = "string";
  private static final Logger logger = LoggerFactory.getLogger(FacetSearchService.class);

  @Autowired
  DatabaseClientHolder databaseClientHolder;
  @Autowired
  SqlExecutionerService executioner;
  @Autowired
  ExplorerConfig explorerConfig;

  public List<String> getFacetValues(FacetSearchQuery fsQuery) {
    List<String> facetValues = new ArrayList<>();
    DatabaseClient client = databaseClientHolder.getDatabaseClient();
    String sqlQuery = generateSqlQuery(fsQuery);
    if (sqlQuery != null) {
      JsonNode queryResults = executioner.executeSqlQuery(client, sqlQuery).get();
      logger.debug(String.format("%s server response:", sqlQuery));
      logger.debug(queryResults.asText());

      facetValues = parseFacetQueryResults(queryResults, fsQuery);
    }
    return facetValues;
  }

  public Map<String, String> getFacetValuesRange(FacetInfo facetInfo) {
    Map<String, String> facetValues = new HashMap<>();
    JsonNode queryResults = null;
    DatabaseClient client = databaseClientHolder.getDatabaseClient();

    Properties props = explorerConfig.getQueryProperties();
    String sqlQuery = props.getProperty("minMaxFacetValuesQuery");

    String entityName = STRING_IDENTIFIER + facetInfo.getSchemaName() + STRING_IDENTIFIER + DOT_CHAR
        + STRING_IDENTIFIER + facetInfo.getEntityName() + STRING_IDENTIFIER;
    String facetName = entityName + DOT_CHAR + STRING_IDENTIFIER + facetInfo.getFacetName()
        + STRING_IDENTIFIER;
    String minFacet = facetInfo.getFacetName() + "Min";
    String maxFacet = facetInfo.getFacetName() + "Max";

    sqlQuery = String.format(sqlQuery, facetName, minFacet, facetName, maxFacet, entityName);

    if (sqlQuery != null) {
      queryResults = executioner.executeSqlQuery(client, sqlQuery).get();

      logger.debug(String.format("%s server response:", sqlQuery));
      logger.debug(queryResults.asText());
    }

    if (queryResults != null) {
      queryResults = queryResults.path("rows").get(0);
      facetValues.put("min", queryResults.get(minFacet).get("value").asText());
      facetValues.put("max", queryResults.get(maxFacet).get("value").asText());

      logger.debug(String.format("%s: %s"), minFacet, facetValues.get("min"));
      logger.debug(String.format("%s: %s"), minFacet, facetValues.get("max"));
    }
    return facetValues;
  }

  private String generateSqlQuery(FacetSearchQuery fsq) {
    String query = null;
    String entityName =
        STRING_IDENTIFIER + fsq.getFacetInfo().getSchemaName() + STRING_IDENTIFIER + DOT_CHAR
            + STRING_IDENTIFIER + fsq.getFacetInfo().getEntityName() + STRING_IDENTIFIER;
    String facetName = entityName + DOT_CHAR + STRING_IDENTIFIER + fsq.getFacetInfo().getFacetName()
        + STRING_IDENTIFIER;
    Properties prop = explorerConfig.getQueryProperties();

    if (fsq.getDataType().equals(STRING_DATATYPE)) {
      query = prop.getProperty("stringFacetValuesQuery");
      if (query != null) {
        query = String.format(query, facetName, entityName, facetName,
            STRING_IDENTIFIER + fsq.getQueryParams().get(0) + ANY_STRING + STRING_IDENTIFIER,
            facetName,
            STRING_IDENTIFIER + ANY_CHAR + ANY_STRING + fsq.getQueryParams().get(0) + ANY_STRING
                + STRING_IDENTIFIER, fsq.getLimit());
      }
    }
    logger.debug(query);
    return query;
  }

  private List<String> parseFacetQueryResults(JsonNode queryResults, FacetSearchQuery fsq) {
    List<String> values = new ArrayList<>();
    String facetName =
        fsq.getFacetInfo().getSchemaName() + DOT_CHAR + fsq.getFacetInfo().getEntityName()
            + DOT_CHAR + fsq.getFacetInfo().getFacetName();
    logger.debug(String.format("%s parsed facetValues:", fsq.getFacetInfo().getFacetName()));
    if (queryResults != null) {
      queryResults.path("rows").forEach(jsonNode -> {
        String value = jsonNode.get(facetName).get("value").asText();
        values.add(value);
        logger.debug(value);
      });
    }
    return values;
  }
}
