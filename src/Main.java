import view.PostmanClone;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PostmanClone().setVisible(true));
    }
}

//import org.apache.hc.client5.http.fluent.*;
//        import org.apache.hc.core5.http.ClassicHttpResponse;
//
//        import java.io.IOException;

//public class FluentApiExample {
//    public static void main(String[] args) {
//        try {
//            ClassicHttpResponse response = Request.get("https://jsonplaceholder.typicode.com/posts/1")
//                    .connectTimeout(1000)
//                    .socketTimeout(1000)
//                    .execute()
//                    .returnResponse();
//
//            System.out.println("Status Code: " + response.getCode());
//            System.out.println("Response Body:\n" + EntityUtils.toString(response.getEntity()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}