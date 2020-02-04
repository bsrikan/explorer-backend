package com.marklogic.hub.explorer.web;

import com.marklogic.hub.explorer.exception.ExplorerException;
import com.marklogic.hub.explorer.model.QueryDoc;
import com.marklogic.hub.explorer.model.QueryModel;
import com.marklogic.hub.explorer.service.QueryDocService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/datahub/v2/query-docs")
public class QueryDocController {

  @Autowired
  private QueryDocService queryDocService;

  @RequestMapping(value = "/save", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<String> saveQueryDoc(@RequestBody QueryModel queryModel) {
    String docUri = queryModel.getDocUri();
    QueryDoc doc = queryModel.getQueryDoc();

    if (doc == null || doc.getQueryName() == null || doc.getQueryName().isEmpty()) {
      throw new ExplorerException(HttpStatus.BAD_REQUEST.value(), "Query Name cannot be empty");
    }

    if (doc.getJsonQuery() == null) {
      throw new ExplorerException(HttpStatus.BAD_REQUEST.value(), "Search Query cannot be null");
    }

    if (doc.getJsonQuery() == null || doc.getJsonQuery().getEntityNames() == null || doc
        .getJsonQuery().getEntityNames().isEmpty()) {
      throw new ExplorerException(HttpStatus.BAD_REQUEST.value(), "Select atleast one entity."
          + "Empty search query cannot be saved");
    }

    if (doc.getEntityProperties() == null || doc.getEntityProperties().isEmpty()) {
      throw new ExplorerException(HttpStatus.BAD_REQUEST.value(),
          "At least one entity property should be selected to display search results");
    }

    queryDocService.saveQueryDoc(queryModel);
    if (docUri.isEmpty()) {
      return new ResponseEntity<>(queryModel.getDocUri(), HttpStatus.CREATED);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
