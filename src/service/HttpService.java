package service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
public class HttpService {

//    private void sendRequest() {
//        String url = urlPanel.getUrl();
//        String method = urlPanel.getMethod();
//        String headers = requestPanel.getHeaders();
//        String body = requestPanel.getBody();
//
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpGet httpGet = new HttpGet(url);
//            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            String responseBody = EntityUtils.toString(httpEntity);
//
//            responsePanel.setResponse(responseBody);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
