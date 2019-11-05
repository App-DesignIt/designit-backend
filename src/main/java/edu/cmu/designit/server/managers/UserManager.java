package edu.cmu.designit.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.exceptions.AppUnauthorizedException;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.Session;
import edu.cmu.designit.server.models.User;
import edu.cmu.designit.server.utils.MongoPool;
import edu.cmu.designit.server.utils.PasswordUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.lang.String;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserManager extends Manager {
    public static UserManager _self;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> draftCollection;

    public UserManager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
        this.draftCollection = MongoPool.getInstance().getCollection("drafts");
    }

    public static UserManager getInstance(){
        if (_self == null)
            _self = new UserManager();
        return _self;
    }


    public void createUser(User user) throws AppException {
        try {
            JSONObject json = new JSONObject(user);

            Document newDoc = new Document()
                    .append("fullName", user.getFullName())
                    .append("email", user.getEmail())
                    .append("roleId", user.getRoleId())
                    .append("password", user.getPassword())
                    .append("salt", user.getSalt());
            if (newDoc != null) {
                userCollection.insertOne(newDoc);
            }
            else {
                throw new AppInternalServerException(0, "Failed to create new user");
            }
        } catch (Exception e) {
            throw handleException("Create User", e);
        }
    }

    public void updateUser(HttpHeaders headers, User user) throws AppException {
        try {
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(user.getId())) {
                throw new AppUnauthorizedException(70,"Invalid user id");
            }

            Bson filter = new Document("_id", new ObjectId(user.getId()));
            Bson newValue = new Document()
                    .append("fullName", user.getFullName())
                    .append("email", user.getEmail())
                    .append("roleId", user.getRoleId());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                userCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update user details");

        } catch(Exception e) {
            throw handleException("Update User", e);
        }
    }


    public void deleteUser(String userId) throws AppException {
        try {
            Bson filter = new Document("userId", userId);
            userCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete User", e);
        }
    }

    public ArrayList<User> getUserList() throws AppException {
        try{
            FindIterable<Document> userDocs = userCollection.find();
            return convertDocsToArrayList(userDocs);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<Draft> getDraftListByUserId(String userId) throws AppException {
        try{
            Bson filter = new Document("userId", userId);
            FindIterable<Document> draftDocs = draftCollection.find(filter);
            ArrayList<Draft> draftList = DraftManager.getInstance().convertDocsToArrayList(draftDocs);
            return draftList;
        } catch (Exception e) {
            throw handleException("Get Draft List By User Id", e);
        }
    }

    public ArrayList<User> getUserById(String id) throws AppException {
        try{
            Bson filter = new Document("_id", new ObjectId(id));
            FindIterable<Document> userDocs = userCollection.find(filter);
            return convertDocsToArrayList(userDocs);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserByEmail(String email) throws AppException {
        try{
            Bson filter = new Document("email", email);
            FindIterable<Document> userDocs = userCollection.find(filter);
            return convertDocsToArrayList(userDocs);
        } catch(Exception e){
            throw handleException("Get User List by email", e);
        }
    }

    public ArrayList<User> getUserByRole(int roleId) throws AppException {
        try{
            Bson filter = new Document("roleId", roleId);
            FindIterable<Document> userDocs = userCollection.find(filter);
            return convertDocsToArrayList(userDocs);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserListSorted(String sortBy, String direction) throws AppException {
        try{
            BasicDBObject sortParams = new BasicDBObject();
            //1 asending order, -1 desending order
            int directionInt = "asc".equals(direction) ? 1 : -1;
            sortParams.put(sortBy, directionInt);
            FindIterable<Document> userDocs = userCollection.find().sort(sortParams);
            return convertDocsToArrayList(userDocs);
        } catch (Exception e){
            throw handleException("Get Sorted User List", e);
        }
    }

    public ArrayList<User> getUserListPaginated(Integer offset, Integer count) throws AppException {
        try {
            FindIterable<Document> userDocs = userCollection.find().skip(offset).limit(count);
            return convertDocsToArrayList(userDocs);
        } catch (Exception e){
            throw handleException("Get Paginated User List", e);
        }
    }

    public boolean checkAuthentication(String email, String password) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            Bson filter = new Document("email", email);
            FindIterable<Document> userDocs = userCollection.find(filter);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("fullName"),
                        userDoc.getString("email"),
                        userDoc.getInteger("roleId")
                );
                user.setPassword(userDoc.getString("password"));
                user.setSalt(userDoc.getString("salt"));
                userList.add(user);
            }
            return PasswordUtils.verifyUserPassword(password, userList.get(0).getPassword(), userList.get(0).getSalt());
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    private ArrayList<User> convertDocsToArrayList(FindIterable<Document> userDocs) {
        ArrayList<User> userList = new ArrayList<>();
        for(Document userDoc: userDocs) {
            User user = new User(
                    userDoc.getObjectId("_id").toString(),
                    userDoc.getString("fullName"),
                    userDoc.getString("email"),
                    userDoc.getInteger("roleId")
            );
            userList.add(user);
        }
        return userList;
    }

    public void resetUsers() {
        userCollection.drop();
    }
}
