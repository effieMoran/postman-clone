package model;

public class Request {
    private String id;
    private String method;
    private String url;
    private String headers;
    private String body;
    private String folder;

    public Request(String method, String url, String headers, String body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
    }

    public Request(String id, String method, String url, String headers, String body, String folder) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.id = id;
        this.folder = folder;
    }
    public Request(String method, String url, String headers, String body, String folder) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.folder = folder;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

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

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                ", folder='" + folder + '\'' +
                '}';
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
