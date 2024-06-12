package config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DataBaseConfiguration {
    private static final String DATABASE_NAME = "postman_clone";
    private MongoClient mongoClient;
    private MongoDatabase database;

    public DataBaseConfiguration() {
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase(DATABASE_NAME);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
