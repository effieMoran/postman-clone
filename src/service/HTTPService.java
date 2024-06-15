package service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HTTPService {

    private HttpClient httpClient;

    public HTTPService() {
        this.httpClient = HttpClients.createDefault();
    }

    public HttpRequestBase createRequest(String method, String url, String requestBody) throws IOException {
        HttpRequestBase request;
        switch (method.toUpperCase()) {
            case "GET":
                request = new HttpGet(url);
                break;
            case "POST":
                HttpPost postRequest = new HttpPost(url);
                postRequest.setEntity(new StringEntity(requestBody));
                request = postRequest;
                break;
            case "PUT":
                HttpPut putRequest = new HttpPut(url);
                putRequest.setEntity(new StringEntity(requestBody));
                request = putRequest;
                break;
            case "DELETE":
                request = new HttpDelete(url);
                break;
            default:
                throw new IllegalArgumentException("Invalid method: " + method);
        }
        return request;
    }

    public void addHeaders(HttpRequestBase request, String headers) {
        if (!headers.isEmpty()) {
            String[] headerLines = headers.split("\n");
            for (String headerLine : headerLines) {
                String[] parts = headerLine.split(":", 2);
                if (parts.length == 2) {
                    request.addHeader(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }

    public String executeRequest(HttpRequestBase request) throws IOException {
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }
}
