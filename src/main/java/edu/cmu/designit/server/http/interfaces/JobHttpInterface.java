package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class JobHttpInterface extends HttpInterface {
  private ObjectWriter ow;
  private MongoCollection<Document> jobCollection = null;

  public JobHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }


}
