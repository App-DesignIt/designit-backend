package edu.cmu.designit.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.*;
import edu.cmu.designit.server.models.*;
import edu.cmu.designit.server.utils.*;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;
import sun.reflect.annotation.ExceptionProxy;

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

            User newUser = new User(
                    null,
                    json.getString("fullName"),
                    json.getString("email"),
                    json.getInt("roleId"),
                    mySecurePassword,
                    salt,
                    json.getString("bankAccount")
            );
            UserManager.getInstance().createUser(newUser);
            return new AppResponse("Insert Successful");

        }catch (Exception e){
            throw handleException("POST users", e);
        }
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getUsers(@Context HttpHeaders headers,
                                @QueryParam("sortBy") String sortBy,
                                @QueryParam("direction") String direction,
                                @QueryParam("offset") Integer offset,
                                @QueryParam("count") Integer count,
                                @QueryParam("pageSize") Integer pageSize,
                                @QueryParam("page") Integer page,
                                @QueryParam("role") Integer role){
        try {
            AppLogger.info("Got an API call");
            ArrayList<User> users = null;
            if(sortBy != null) {
                if(direction != null) {
                    users = UserManager.getInstance().getUserListSorted(sortBy, direction);
                } else {
                    users = UserManager.getInstance().getUserListSorted(sortBy, "des");
                }
            } else if(offset != null && count != null) {
                users = UserManager.getInstance().getUserListPaginated(offset, count);
            } else if(pageSize != null && page != null) {
                int tempBegin = (page - 1) * pageSize + 1;
                users = UserManager.getInstance().getUserListPaginated(tempBegin, pageSize);
            } else if(role != null) {
                users = UserManager.getInstance().getUserByRole(role);
            } else {
                users = UserManager.getInstance().getUserList();
            }

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        } catch (Exception e) {
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

    @GET
    @Path("/{userId}/comments")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getUserComment(@Context HttpHeaders headers, @PathParam("userId") String userId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Comment> comments = CommentManager.getInstance().getCommentByUserId(userId);

            if(comments != null)
                return new AppResponse(comments);
            else
                throw new HttpBadRequestException(0, "Problem with getting all comments from one user");
        } catch (Exception e){
            throw handleException("GET /{userId}/comments", e);
        }
    }

    @GET
    @Path("/likes")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getLikes(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Like> likes = LikeManager.getInstance().getLikeList();
            if(likes != null)
                return new AppResponse(likes);
            else
                throw new HttpBadRequestException(0, "Problem with getting likes list");
        } catch (Exception e) {
            throw handleException("GET /likes", e);
        }
    }

    @GET
    @Path("/{userId}/drafts")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getDraftsByUserId(@Context HttpHeaders headers, @PathParam("userId") String userId) {
        try {
            ArrayList<Draft> drafts = UserManager.getInstance().getDraftListByUserId(userId);
            return new AppResponse(drafts);
        } catch (Exception e) {
            throw handleException("GET /users/{userId}/drafts", e);
        }
    }

    @GET
    @Path("/{userId}/challenge_submissions")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getChallengeSubmissionsByUserId(@Context HttpHeaders headers, @PathParam("userId") String userId) {
        try {
            ArrayList<ChallengeSubmission> challengeSubmissions = UserManager.getInstance().getChallengeSubmissionListByUserId(userId);
            return new AppResponse(challengeSubmissions);
        } catch (Exception e) {
            throw handleException("GET /users/{userId}/challenge_submissions", e);
        }
    }

    @GET
    @Path("/{userId}/payments")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getPaymentsByRecruiterId(@Context HttpHeaders headers, @PathParam("userId") String userId) {
        try {
            ArrayList<Payment> payments = PaymentManager.getInstance().getPaymentsByRecruiterId(userId);
            return new AppResponse(payments);
        } catch (Exception e) {
            throw handleException("GET /users/{userId}/payments", e);
        }
    }

    @GET
    @Path("/{userId}/likes")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getLikeByLiker(@Context HttpHeaders headers, @PathParam("userId") String userId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Like> likes = LikeManager.getInstance().getLikeByUser(userId);

            if(likes != null)
                return new AppResponse(likes);
            else
                throw new HttpBadRequestException(0, "Problem with getting like by userId");
        } catch (Exception e) {
            throw handleException("GET /users/{userId}/likes", e);
        }
    }

    @DELETE
    @Path("/{userId}/likes")
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteLikes(@PathParam("userId") String userId, @QueryParam("draftId") String draftId){
        try {
            LikeManager.getInstance().deleteLike(draftId, userId);
            return new AppResponse("Delete Successful");
        } catch (Exception e){
            throw handleException("DELETE likes/", e);
        }
    }

    @PATCH
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchUsers(@Context HttpHeaders headers, Object request, @PathParam("id") String id){
        JSONObject json = null;
        try{
            json = new JSONObject(ow.writeValueAsString(request));
            User user = new User(
                    id,
                    json.getString("fullName"),
                    json.getString("email"),
                    json.getInt("roleId")
            );

            UserManager.getInstance().updateUser(headers, user);

        }catch (Exception e){
            throw handleException("PATCH users/{id}", e);
        }

        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{userId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteUsers(@PathParam("userId") String userId){
        try {
            UserManager.getInstance().deleteUser(userId);
            return new AppResponse("Delete Successful");
        } catch (Exception e){
            throw handleException("DELETE users/{userId}", e);
        }
    }

    @DELETE
    @Path("/reset")
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse resetUsers() {
        try {
            UserManager.getInstance().resetUsers();
            return new AppResponse("Reset Successful");
        } catch (Exception e) {
            throw handleException("Reset drafts", e);
        }
    }
}
