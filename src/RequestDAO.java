import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public RequestDAO() {
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("postman_clone");
        collection = database.getCollection("requests");
    }

    public void saveRequest(Request request, String folderName) {
        Document doc = new Document("method", request.getMethod())
                .append("url", request.getUrl())
                .append("headers", request.getHeaders())
                .append("body", request.getBody())
                .append("folder", folderName); // Include folder information
        collection.insertOne(doc);
    }

    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        for (Document doc : collection.find()) {
            Request request = new Request(
                    doc.getString("method"),
                    doc.getString("url"),
                    doc.getString("headers"),
                    doc.getString("body"));
            // Set folder information
            request.setFolder(doc.getString("folder"));
            requests.add(request);
        }
        return requests;
    }

    public static class Request {
            private String method;
            private String url;
            private String headers;
            private String body;
            private String folder; // Add folder field

            public Request(String method, String url, String headers, String body) {
                this.method = method;
                this.url = url;
                this.headers = headers;
                this.body = body;
            }

        public Request(String method, String url, String headers, String body, String folder) {
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
            this.folder = folder;
        }
            // Getter and setter methods for folder
            public String getFolder() {
                return folder;
            }

            public void setFolder(String folder) {
                this.folder = folder;
            }

            // Getter methods for other fields
            public String getMethod() {
                return method;
            }

            public String getUrl() {
                return url;
            }

            public String getHeaders() {
                return headers;
            }

            public String getBody() {
                return body;
            }
        }


}
