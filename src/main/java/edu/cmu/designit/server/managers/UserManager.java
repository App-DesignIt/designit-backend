package edu.cmu.designit.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.User;
import edu.cmu.designit.server.utils.MongoPool;
import edu.cmu.designit.server.utils.PasswordUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.lang.String;
import java.util.ArrayList;

public class UserManager extends Manager {
    public static UserManager _self;
    private MongoCollection<Document> userCollection;


    public UserManager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
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
                    .append("userId", user.getUserId())
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

    public void updateUser(User user) throws AppException {
        try {
            Bson filter = new Document("userId", user.getUserId());
            Bson newValue = new Document()
                    .append("userId", user.getUserId())
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
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("userId"),
                        userDoc.getString("fullName"),
                        userDoc.getString("email"),
                        userDoc.getString("roleId")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserById(String id) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            Bson filter = new Document("userId", id);
            FindIterable<Document> userDocs = userCollection.find(filter);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("userId"),
                        userDoc.getString("fullName"),
                        userDoc.getString("email"),
                        userDoc.getString("roleId")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserByRole(String roleId) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            Bson filter = new Document("roleId", roleId);
            FindIterable<Document> userDocs = userCollection.find(filter);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("userId"),
                        userDoc.getString("fullName"),
                        userDoc.getString("email"),
                        userDoc.getString("roleId")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserListSorted(String sortBy, String direction) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            //1 asending order, -1 desending order
            int directionInt = "asc".equals(direction) ? 1 : -1;
            sortParams.put(sortBy, directionInt);
            FindIterable<Document> userDocs = userCollection.find().sort(sortParams);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("userId"),
                        userDoc.getString("fullName"),
                        userDoc.getString("email"),
                        userDoc.getString("roleId")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch (Exception e){
            throw handleException("Get Sorted User List", e);
        }
    }

    public ArrayList<User> getUserListPaginated(Integer offset, Integer count) throws AppException {
        try {
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find().skip(offset).limit(count);
            for(Document userDoc: userDocs) {
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("userId"),
                        userDoc.getString("fullName"),
                        userDoc.getString("email"),
                        userDoc.getString("roleId")
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch (Exception e){
            throw handleException("Get Paginated User List", e);
        }
    }

    public boolean checkAuthentication(String id, String password) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                if(userDoc.getString("userId").equals(id)) {
                    User user = new User(
                            userDoc.getObjectId("_id").toString(),
                            userDoc.getString("userId"),
                            userDoc.getString("fullName"),
                            userDoc.getString("email"),
                            userDoc.getString("roleId")
                    );
                    user.setPassword(userDoc.getString("password"));
                    user.setSalt(userDoc.getString("salt"));
                    userList.add(user);
                }
            }
            return PasswordUtils.verifyUserPassword(password, userList.get(0).getPassword(), userList.get(0).getSalt());
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }
}
