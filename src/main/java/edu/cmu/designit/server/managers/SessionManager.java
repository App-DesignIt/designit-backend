package edu.cmu.designit.server.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.*;
import edu.cmu.designit.server.models.Session;
import edu.cmu.designit.server.models.User;
import edu.cmu.designit.server.utils.MongoPool;
import edu.cmu.designit.server.utils.PasswordUtils;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.List;

public class SessionManager extends Manager {
  private static SessionManager _self;
  private ObjectWriter ow;
  private MongoCollection<Document> userCollection;
  private static HashMap<String, Session> sessionMap = new HashMap<>();

  public SessionManager() {
    this.userCollection = MongoPool.getInstance().getCollection("users");
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  public static SessionManager getInstance(){
    if (_self == null)
      _self = new SessionManager();
    return _self;
  }

  public Session create(Object request) throws AppException {
    try {
      JSONObject json = new JSONObject(ow.writeValueAsString(request));
      if (!json.has("email"))
        throw new AppBadRequestException(55, "missing email");
      if (!json.has("password"))
        throw new AppBadRequestException(55, "missing password");

      String email = json.getString("email");
      String password = json.getString("password");
      BasicDBObject query = new BasicDBObject();
      query.put("email", email);
      Document item = userCollection.find(query).first();

      if (item == null) {
        throw new AppNotFoundException(0, "No user found matching credentials");
      }

      User user = new User(
              item.getObjectId("_id").toString(),
              item.getString("fullName"),
              item.getString("email"),
              item.getInteger("roleId")
      );
      user.setPassword(item.getString("password"));
      user.setSalt(item.getString("salt"));

      if(PasswordUtils.verifyUserPassword(password, user.getPassword(), user.getSalt())) {
        throw new AppUnauthorizedException(0, "Authentication failed");
      }

      Session sessionVal = new Session(user);
      sessionMap.put(sessionVal.getToken(), sessionVal);
      return sessionVal;
    } catch (JsonProcessingException e) {
      throw new AppBadRequestException(33, e.getMessage());
    } catch (AppBadRequestException e) {
      throw e;
    } catch (AppNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new AppInternalServerException(0, e.getMessage());
    }
  }

  public Session getSessionForToken(HttpHeaders headers) throws Exception{
    List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
    if (authHeaders == null)
      throw new AppUnauthorizedException(70,"No Authorization Headers");
    String token = authHeaders.get(0);

    if(SessionManager.getInstance().sessionMap.containsKey(token))
      return SessionManager.getInstance().sessionMap.get(token);
    else
      throw new AppUnauthorizedException(70,"Invalid Token");
  }

}
