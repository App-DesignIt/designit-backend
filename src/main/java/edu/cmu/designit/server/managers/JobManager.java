package edu.cmu.designit.server.managers;

import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.Job;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;

import java.util.Date;

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


    }catch(Exception e){
      throw handleException("Create Draft", e);
    }
  }


}
