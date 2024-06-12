package model;

import org.bson.types.ObjectId;

public class Request {
    private String id; // Add ID field
    private String method;
    private String url;
    private String headers;
    private String body;
    private String folder;

    // Constructors
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

    // Getter and setter methods for ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter methods for folder
    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    // Getter and setter methods for other fields
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
