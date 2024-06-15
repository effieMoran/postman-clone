package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.DataBaseConfiguration;
import model.Request;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoRequestDao implements Dao<Request> {
    private static final String METHOD = "method";
    private static final String URL = "url";
    private static final String HEADERS = "headers";
    private static final String BODY = "body";
    private static final String FOLDER = "folder";
    private static final String ID = "_id";

    private MongoCollection<Document> collection;

    public MongoRequestDao(DataBaseConfiguration dbConfig) {
        MongoDatabase database = dbConfig.getDatabase();
        collection = database.getCollection("requests");
    }


    @Override
    public List<Request> getAll() {
        List<Request> requests = new ArrayList<>();
        for (Document doc : collection.find()) {
            requests.add(documentToRequest(doc));
        }
        return requests;
    }

    @Override
    public Request save(Request request) {
        Document doc = new Document(METHOD, request.getMethod())
                .append(URL, request.getUrl())
                .append(HEADERS, request.getHeaders())
                .append(BODY, request.getBody())
                .append(FOLDER, request.getFolder());
        collection.insertOne(doc);
        ObjectId id = doc.getObjectId(ID);
        request.setId(id.toHexString());
        return request;
    }

    @Override
    public void update(Request request) {
        // Retrieve the existing request to check the current state
        Request exists = this.get(request.getId());
        System.out.println(exists);
        System.out.println(request);

        // Create a Document with the updated fields from the request
        Document updatedDoc = new Document()
                .append(METHOD, request.getMethod())
                .append(URL, request.getUrl())
                .append(HEADERS, request.getHeaders())
                .append(BODY, request.getBody())
                .append(FOLDER, request.getFolder());

        // Convert the request ID to ObjectId
        ObjectId objectId = new ObjectId(request.getId());

        // Perform the update operation in the collection
        com.mongodb.client.result.UpdateResult result = collection.updateOne(
                new Document(ID, objectId),
                new Document("$set", updatedDoc)
        );

        // Check if the update was successful
        if (result.getModifiedCount() > 0) {
            System.out.println("Request updated successfully!");
        } else {
            // TODO; throw an exception
            System.out.println("No request was updated.");
            System.out.println("Matched count: " + result.getMatchedCount());
            System.out.println("Modified count: " + result.getModifiedCount());
            System.out.println("Acknowledged: " + result.wasAcknowledged());
        }
    }

    @Override
    public Request get(String id) {
        Document doc = collection.find(new Document(ID, new ObjectId(id))).first();
        if (doc != null) {
            String method = doc.getString(METHOD);
            String url = doc.getString(URL);
            String headers = doc.getString(HEADERS);
            String body = doc.getString(BODY);
            String folder = doc.getString(FOLDER);

            return new Request(id, method, url, headers, body, folder);
        } else {
            // Document not found, return null or throw an exception as per your requirement
            return null;
        }
    }

    @Override
    public void delete(Request request) {
        collection.deleteOne(new Document(ID, request.getId()));
    }

    @Override
    public void delete(String id) {
        ObjectId objectId = new ObjectId(id);
        collection.deleteOne(new Document(ID, objectId));
    }
    private Request documentToRequest(Document doc) {
        Request request = new Request(
                doc.getString(METHOD),
                doc.getString(URL),
                doc.getString(HEADERS),
                doc.getString(BODY),
                doc.getString(FOLDER));
        request.setId(doc.getObjectId(ID).toHexString());
        return request;
    }
}
