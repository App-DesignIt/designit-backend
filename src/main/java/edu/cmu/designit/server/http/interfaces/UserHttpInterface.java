package edu.cmu.designit.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.models.User;
import edu.cmu.designit.server.managers.UserManager;
import edu.cmu.designit.server.utils.*;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/users")

public class UserHttpInterface extends HttpInterface{

    private ObjectWriter ow;
    private MongoCollection<Document> userCollection = null;

    public UserHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postUsers(Object request){
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            // Generate Salt. The generated value can be stored in DB.
            String salt = PasswordUtils.getSalt(30);

            // Protect user's password. The generated value can be stored in DB.
            String mySecurePassword = PasswordUtils.generateSecurePassword(json.getString("password"), salt);

            // Print out protected password
            System.out.println("My secure password = " + mySecurePassword);
            System.out.println("Salt value = " + salt);

            User newUser = new User(
                    null,
                    json.getString("userId"),
                    json.getString("fullName"),
                    json.getString("email"),
                    json.getString("roleId"),
                    mySecurePassword,
                    salt
            );
            UserManager.getInstance().createUser(newUser);
            return new AppResponse("Insert Successful");

        }catch (Exception e){
            throw handleException("POST users", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getUsers(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<User> users = UserManager.getInstance().getUserList();

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        } catch (Exception e){
            throw handleException("GET /users", e);
        }
    }

    @GET
    @Path("/{userId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleUser(@Context HttpHeaders headers, @PathParam("userId") String userId){
        try{
            AppLogger.info("Got an API call");
            ArrayList<User> users = UserManager.getInstance().getUserById(userId);

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        }catch (Exception e){
            throw handleException("GET /users/{userId}", e);
        }
    }


    @PATCH
    @Path("/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchUsers(Object request, @PathParam("userId") String userId){

        JSONObject json = null;

        try{
            json = new JSONObject(ow.writeValueAsString(request));
            User user = new User(
                    null,
                    userId,
                    json.getString("fullName"),
                    json.getString("email"),
                    json.getString("roleId")
            );

            UserManager.getInstance().updateUser(user);

        }catch (Exception e){
            throw handleException("PATCH users/{userId}", e);
        }

        return new AppResponse("Update Successful");
    }




    @DELETE
    @Path("/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteUsers(@PathParam("userId") String userId){

        try{
            UserManager.getInstance().deleteUser(userId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE users/{userId}", e);
        }
    }

    @GET
    @Path("/login")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse checkAuthentication(Object request){
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            boolean result = UserManager.getInstance()
                    .checkAuthentication(json.getString("userId"), json.getString("password"));
            if(result)
                return new AppResponse("Success");
            else
                return new AppResponse("Failed");
        } catch (Exception e){
            throw handleException("GET /users", e);
        }
    }
}
