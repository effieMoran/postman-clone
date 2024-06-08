import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    private List<Request> requests;

    public RequestDAO() {
        this.requests = new ArrayList<>();
    }

    public void saveRequest(Request request) {
        requests.add(request);
    }

    public List<Request> getAllRequests() {
        return requests;
    }

    public static class Request {
        private String method;
        private String url;
        private String headers;
        private String body;

        public Request(String method, String url, String headers, String body) {
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
        }

        // Getters and setters
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
