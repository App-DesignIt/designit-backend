package edu.cmu.designit.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.designit.server.exceptions.AppException;
import edu.cmu.designit.server.exceptions.AppInternalServerException;
import edu.cmu.designit.server.models.Payment;
import edu.cmu.designit.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Date;

public class PaymentManager extends Manager {

    private static PaymentManager _self;
    private MongoCollection<Document> paymentCollection;


    private PaymentManager() {
        this.paymentCollection = MongoPool.getInstance().getCollection("payments");
    }

    public static PaymentManager getInstance(){
        if (_self == null)
            _self = new PaymentManager();
        return _self;
    }

    public void createPayment(Payment payment) throws AppException {
        try {
            Document newDoc = new Document()
                    .append("userId", payment.getUserId())
                    .append("amount", payment.getAmount())
                    .append("status", payment.getStatus())
                    .append("recruiterId", payment.getRecruiterId())
                    .append("challengeId", payment.getChallengeId())
                    .append("submissionId", payment.getSubmissionId())
                    .append("createTime", payment.getCreateTime())
                    .append("modifyTime", payment.getModifyTime());
            if (newDoc != null) {
                paymentCollection.insertOne(newDoc);
            } else {
                throw new AppInternalServerException(0, "Failed to create new payment relation");
            }
        } catch (Exception e) {
            throw handleException("Create payment", e);
        }
    }

    public ArrayList<Payment> getPaymentList() throws AppException {
        try {
            return convertDocsToArrayList(paymentCollection.find());
        } catch(Exception e) {
            throw handleException("Get Payment List", e);
        }
    }

    public ArrayList<Payment> getPaymentById(String id) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(id));
            return convertDocsToArrayList(paymentCollection.find(filter));
        } catch (Exception e){
            throw handleException("Get Single Payment", e);
        }
    }

    public ArrayList<Payment> getPaymentByChallengeId(String challengeId) throws AppException {
        try {
            Bson filter = new Document("challengeId", challengeId);
            return convertDocsToArrayList(paymentCollection.find(filter));
        } catch (Exception e) {
            throw handleException("Get Payment By Challenge Id", e);
        }
    }

    public void makePayment(String paymentId) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(paymentId));
            Bson newValue = new Document()
                    .append("status", 1)
                    .append("modifyTime", new Date());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                paymentCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update payment details");
        } catch (Exception e) {
            throw handleException("Make Payment", e);
        }
    }

    public void updatePayment(Payment payment) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(payment.getId()));
            Bson newValue = new Document()
                    .append("status", payment.getStatus())
                    .append("modifyTime", payment.getModifyTime());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                paymentCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update payment details");

        } catch (Exception e) {
            throw handleException("Update Comment", e);
        }
    }

    public void deletePayment(String id) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(id));
            paymentCollection.deleteOne(filter);
        } catch (Exception e){
            throw handleException("Delete payment", e);
        }
    }

    public ArrayList<Payment> getPaymentsByRecruiterId(String recruiterId) throws AppException {
        try {
            Bson filter = new Document("recruiterId", recruiterId);
            return convertDocsToArrayList(paymentCollection.find(filter));
        } catch (Exception e) {
            throw handleException("Get Payments By Recruiter Id", e);
        }
    }

    private ArrayList<Payment> convertDocsToArrayList(FindIterable<Document> paymentDocs) {
        ArrayList<Payment> paymentList = new ArrayList<>();
        for(Document paymentDoc: paymentDocs) {
            Payment payment = new Payment(
                    paymentDoc.getObjectId("_id").toString(),
                    paymentDoc.getString("userId"),
                    paymentDoc.getDouble("amount"),
                    paymentDoc.getInteger("status"),
                    paymentDoc.getString("recruiterId"),
                    paymentDoc.getString("challengeId"),
                    paymentDoc.getString("submissionId"),
                    (Date) paymentDoc.get("createTime"),
                    (Date) paymentDoc.get("modifyTime")
            );
            paymentList.add(payment);
        }
        return paymentList;
    }
}
