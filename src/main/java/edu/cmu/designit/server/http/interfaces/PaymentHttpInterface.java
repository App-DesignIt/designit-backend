package edu.cmu.designit.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.http.exceptions.HttpBadRequestException;
import edu.cmu.designit.server.http.responses.AppResponse;
import edu.cmu.designit.server.http.utils.PATCH;
import edu.cmu.designit.server.managers.PaymentManager;
import edu.cmu.designit.server.models.Payment;
import edu.cmu.designit.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;

@Path("/payments")
public class PaymentHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> commentCollection = null;

    public PaymentHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postPayments(Object request){
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Date date = new Date();
            Payment payment = new Payment (
                    null,
                    json.getString("userId"),
                    json.getDouble("amount"),
                    json.getInt("status"),
                    json.getString("recruiterId"),
                    json.getString("submissionId"),
                    date,
                    date
            );

            PaymentManager.getInstance().createPayment(payment);
            return new AppResponse("Insert Successful");
        } catch (Exception e) {
            throw handleException("POST comments", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getPayments(@Context HttpHeaders headers){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Payment> comments = PaymentManager.getInstance().getPaymentList();
            if(comments != null)
                return new AppResponse(comments);
            else
                throw new HttpBadRequestException(0, "Problem with getting payments list");
        } catch (Exception e) {
            throw handleException("GET /payments", e);
        }
    }

    @GET
    @Path("/{paymentId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSinglePayment(@Context HttpHeaders headers, @PathParam("paymentId") String paymentId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Payment> payments = PaymentManager.getInstance().getPaymentById(paymentId);

            if(payments != null)
                return new AppResponse(payments);
            else
                throw new HttpBadRequestException(0, "Problem with getting single payment");
        } catch (Exception e){
            throw handleException("GET /payments/{paymentId}", e);
        }
    }

    @GET
    @Path("/users/{userId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getPaymentByUser(@Context HttpHeaders headers, @PathParam("userId") String userId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Payment> payments = PaymentManager.getInstance().getPaymentByUser(userId);

            if(payments != null)
                return new AppResponse(payments);
            else
                throw new HttpBadRequestException(0, "Problem with getting single payment");
        } catch (Exception e){
            throw handleException("GET /payments/{paymentId}", e);
        }
    }

    @GET
    @Path("/recruiters/{recruiterId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getPaymentByRecruiter(@Context HttpHeaders headers, @PathParam("recruiterId") String recruiterId){
        try {
            AppLogger.info("Got an API call");
            ArrayList<Payment> payments = PaymentManager.getInstance().getPaymentByRecruiter(recruiterId);

            if(payments != null)
                return new AppResponse(payments);
            else
                throw new HttpBadRequestException(0, "Problem with getting single payment");
        } catch (Exception e){
            throw handleException("GET /payments/{paymentId}", e);
        }
    }

    @PATCH
    @Path("/{paymentId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchPayment(Object request, @PathParam("paymentId") String paymentId){
        JSONObject json = null;
        try {
            ArrayList<Payment> paymentList = PaymentManager.getInstance().getPaymentById(paymentId);
            if(paymentList.isEmpty()) {
                return new AppResponse("No such payment with given Id: " + paymentId);
            }
            json = new JSONObject(ow.writeValueAsString(request));
            Date date = new Date();
            Payment payment = new Payment (
                    paymentId,
                    null,
                    null,
                    json.getInt("status"),
                    null,
                    null,
                    null,
                    date
            );
            PaymentManager.getInstance().updatePayment(payment);
        } catch (Exception e){
            throw handleException("PATCH payments/{paymentId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{paymentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteComment(@PathParam("paymentId") String paymentId){
        try {
            PaymentManager.getInstance().deletePayment(paymentId);
            return new AppResponse("Delete Successful");
        } catch (Exception e){
            throw handleException("DELETE payments/{paymentId}", e);
        }
    }
}
