package edu.cmu.designit.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Job;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;

public class JobManager extends Manager {
  public static JobManager _self;
  private MongoCollection<Document> jobCollection;

  public JobManager() {
    this.jobCollection = MongoPool.getInstance().getCollection("jobs");
  }

  public static JobManager getInstance(){
    if (_self == null)
      _self = new JobManager();
    return _self;
  }

  public void createJob(Job job) throws AppException {
    try{
      Document newDoc = new Document()
              .append("name", job.getName())
              .append("recruiterId", job.getRecruiterId())
              .append("companyName", job.getCompanyName())
              .append("description", job.getDescription())
              .append("address", job.getAddress());
      if (newDoc != null)
        jobCollection.insertOne(newDoc);
      else
        throw new AppInternalServerException(0, "Failed to create new job");
    }catch(Exception e){
      throw handleException("Create Draft", e);
    }
  }

  public ArrayList<Job> getJobById(String id) throws AppException {
    try{
      Bson filter = new Document("_id", new ObjectId(id));
      FindIterable<Document> jobDocs = jobCollection.find(filter);
      return convertDocsToArrayList(jobDocs);
    } catch(Exception e){
      throw handleException("Get Job List", e);
    }
  }

  public ArrayList<Job> getJobList() throws AppException {
    try{
      FindIterable<Document> jobDocs = jobCollection.find();
      return convertDocsToArrayList(jobDocs);
    } catch(Exception e){
      throw handleException("Get Job List", e);
    }
  }

  public ArrayList<Job> getJobListSorted(String sortby, String direction) throws AppException {
    try {
      BasicDBObject sortParams = new BasicDBObject();
      int directionInt = direction.equals("asc") ? 1 : -1;
      sortParams.put(sortby, directionInt);

      FindIterable<Document> jobDocs = jobCollection.find().sort(sortParams);
      return convertDocsToArrayList(jobDocs);
    } catch (Exception e) {
      throw handleException("Get Job List Sorted", e);
    }
  }

  public ArrayList<Job> getJobListPaginated(Integer offset, Integer count) throws AppException {
    try {
      FindIterable<Document> jobDocs = jobCollection.find().skip(offset).limit(count);
      return convertDocsToArrayList(jobDocs);
    } catch (Exception e) {
      throw handleException("Get Job List Paginated", e);
    }
  }

  public ArrayList<Job> getJobListFiltered(String filter) throws AppException {
    try {
      ArrayList<Job> jobList = new ArrayList<>();
      if(filter.equals("recent")) {
        BasicDBObject sortParams = new BasicDBObject();
        sortParams.put("createTime", -1);
        FindIterable<Document> jobDocs = jobCollection.find().sort(sortParams).limit(100);
        jobList = convertDocsToArrayList(jobDocs);
      } else {
        FindIterable<Document> jobDocs = jobCollection.find();
        jobList = convertDocsToArrayList(jobDocs);
      }
      return jobList;
    } catch (Exception e) {
      throw handleException("Get Job List Filtered", e);
    }
  }

  public void updateJob(Job job) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(job.getId()));
      Bson newValue = new Document()
              .append("name", job.getName())
              .append("recruiterId", job.getRecruiterId())
              .append("companyName", job.getCompanyName())
              .append("description", job.getDescription())
              .append("address", job.getAddress());
      Bson updateOperationDocument = new Document("$set", newValue);

      if (newValue != null)
        jobCollection.updateOne(filter, updateOperationDocument);
      else
        throw new AppInternalServerException(0, "Failed to update job details");

    } catch (Exception e) {
      throw handleException("Update Job", e);
    }
  }

  public void deleteJob(String jobId) throws AppException {
    try {
      Bson filter = new Document("_id", new ObjectId(jobId));
      jobCollection.deleteOne(filter);
    } catch (Exception e) {
      throw handleException("Delete Job", e);
    }
  }

  public ArrayList<Job> convertDocsToArrayList(FindIterable<Document> jobDocs) {
    ArrayList<Job> jobList = new ArrayList<>();
    for(Document jobDoc: jobDocs) {
      Job job = new Job(
              jobDoc.getObjectId("_id").toString(),
              jobDoc.getString("name"),
              jobDoc.getString("recruiterId"),
              jobDoc.getString("companyName"),
              jobDoc.getString("description"),
              jobDoc.getString("address")
      );
      jobList.add(job);
    }
    return jobList;
  }

}
