package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.managers.DraftManager;
import edu.cmu.designit.server.managers.JobManager;
import edu.cmu.designit.server.models.Draft;
import edu.cmu.designit.server.models.Job;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/jobs")
public class JobHttpInterface extends HttpInterface {
  private ObjectWriter ow;
  private MongoCollection<Document> jobCollection = null;

  public JobHttpInterface() {
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse postJobs(Object request){
    try{
      JSONObject json = new JSONObject(ow.writeValueAsString(request));

      Job newJob = new Job(
              json.getString("name"),
              json.getString("recruiterId"),
              json.getString("companyName"),
              json.getString("description"),
              json.getString("address")
      );

      JobManager.getInstance().createJob(newJob);
      return new AppResponse("Insert Successful");

    }catch (Exception e){
      throw handleException("POST jobs", e);
    }
  }

  @GET
  @Path("/{jobId}")
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getSingleJob(@Context HttpHeaders headers, @PathParam("jobId") String jobId){
    try{
      ArrayList<Job> jobs = JobManager.getInstance().getJobById(jobId);
      if(jobs != null)
        return new AppResponse(jobs);
      else
        throw new HttpBadRequestException(0, "Problem with getting drafts");
    }catch (Exception e){
      throw handleException("GET /drafts/{draftId}", e);
    }
  }

  //Sorting: http://localhost:8080/api/jobs?sortby=name&sortdirection=asc
  //Pagination: http://localhost:8080/api/jobs?offset=1&count=2
  //Filter: http://localhost:8080/api/jobs?filter=
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public AppResponse getJobs(@Context HttpHeaders headers, @QueryParam("sortby") String sortby,
                               @QueryParam("sortdirection") String sortdirection,
                               @QueryParam("offset") Integer offset,
                               @QueryParam("count") Integer count,
                               @QueryParam("filter") String filter){

    try{
      ArrayList<Job> jobs = null;
      if(sortby != null) {
        if(sortdirection == null)  {
          jobs = JobManager.getInstance().getJobListSorted(sortby, "desc");
        } else {
          jobs = JobManager.getInstance().getJobListSorted(sortby, "asc");
        }
      } else if (offset != null && count != null ) {
        jobs = JobManager.getInstance().getJobListPaginated(offset, count);
      } else if (filter != null) {
        jobs = JobManager.getInstance().getJobListFiltered(filter);
      } else{
        jobs = JobManager.getInstance().getJobList();
      }

      return new AppResponse(jobs);
    } catch (Exception e){
      throw handleException("GET /jobs", e);
    }
  }


}
