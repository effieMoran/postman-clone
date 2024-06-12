package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.DataBaseConfiguration;
import model.Request;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MongoRequestDao implements Dao<Request> {
    private static final String METHOD = "method";
    private static final String URL = "url";
    private static final String HEADERS = "headers";
    private static final String BODY = "body";
    private static final String FOLDER = "folder";

    private MongoCollection<Document> collection;

    public MongoRequestDao(DataBaseConfiguration dbConfig) {
        MongoDatabase database = dbConfig.getDatabase();
        collection = database.getCollection("requests");
    }

    @Override
    public Optional<Request> get(long id) {
        Document doc = collection.find(new Document("_id", id)).first();
        return doc != null ? Optional.of(documentToRequest(doc)) : Optional.empty();
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
    public void save(Request request) {
        Document doc = new Document()
                .append(METHOD, request.getMethod())
                .append(URL, request.getUrl())
                .append(HEADERS, request.getHeaders())
                .append(BODY, request.getBody())
                .append(FOLDER, request.getFolder());
        collection.insertOne(doc);
    }

    @Override
    public void update(Request request, String[] params) {
        Document updatedDoc = new Document()
                .append(METHOD, Objects.requireNonNull(params[0], "Method cannot be null"))
                .append(URL, Objects.requireNonNull(params[1], "URL cannot be null"))
                .append(HEADERS, Objects.requireNonNull(params[2], "Headers cannot be null"))
                .append(BODY, Objects.requireNonNull(params[3], "Body cannot be null"))
                .append(FOLDER, Objects.requireNonNull(params[4], "Folder cannot be null"));

        collection.updateOne(new Document("_id", request.getId()), new Document("$set", updatedDoc));
    }

    @Override
    public void delete(Request request) {
        collection.deleteOne(new Document("_id", request.getId()));
    }

    private Request documentToRequest(Document doc) {
        Request request = new Request(
                doc.getString(METHOD),
                doc.getString(URL),
                doc.getString(HEADERS),
                doc.getString(BODY),
                doc.getString(FOLDER));
        request.setId(doc.getObjectId("_id").toHexString());
        return request;
    }
}
