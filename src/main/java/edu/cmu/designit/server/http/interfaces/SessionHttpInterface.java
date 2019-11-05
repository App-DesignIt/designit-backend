package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.managers.SessionManager;
import org.json.JSONObject;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sessions")

public class SessionHttpInterface extends MainHttpInterface {
  private SessionManager service;
  private ObjectWriter ow;

  public SessionHttpInterface() {
    service = SessionManager.getInstance();
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @OPTIONS
  @PermitAll
  public Response optionsById() {
    return Response.ok().build();
  }

  //http://localhost:8080/api/sessions/login
  @POST
  @Path("/login")
  @Consumes({ MediaType.APPLICATION_JSON})
  @Produces({ MediaType.APPLICATION_JSON})
  public AppResponse create(Object request) throws Exception{
    return new AppResponse(service.create(request));
  }

}
