import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class PostmanClone extends JFrame {
    private JTextField urlField;
    private JComboBox<String> methodComboBox;
    private JTextArea headersArea;
    private JTextArea requestBodyArea;
    private JTextArea responseTextArea;
    private JButton sendButton;

    private HttpClient httpClient;

    public PostmanClone() {
        httpClient = HttpClients.createDefault();

        setTitle("Postman Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);

        initComponents();
        addComponentsToFrame();

        setVisible(true);
    }

    private void initComponents() {
        urlField = new JTextField(40);
        methodComboBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        headersArea = new JTextArea(5, 30);
        requestBodyArea = new JTextArea(10, 30);
        responseTextArea = new JTextArea(15, 50);
        responseTextArea.setEditable(false);
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendRequest());
    }

    private void addComponentsToFrame() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("URL:"));
        topPanel.add(urlField);
        topPanel.add(new JLabel("Method:"));
        topPanel.add(methodComboBox);
        topPanel.add(sendButton);
        container.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.add(new JScrollPane(headersArea));
        centerPanel.add(new JScrollPane(requestBodyArea));
        centerPanel.add(new JScrollPane(responseTextArea));
        container.add(centerPanel, BorderLayout.CENTER);
    }

    private void sendRequest() {
        String url = urlField.getText();
        String method = (String) methodComboBox.getSelectedItem();
        String headers = headersArea.getText();
        String requestBody = requestBodyArea.getText();

        //TODO: Replace this with a factory method
        try {
            HttpRequestBase request;
            switch (method) {
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

            // Add headers
            if (!headers.isEmpty()) {
                String[] headerLines = headers.split("\n");
                for (String headerLine : headerLines) {
                    String[] parts = headerLine.split(":", 2);
                    if (parts.length == 2) {
                        request.addHeader(parts[0].trim(), parts[1].trim());
                    }
                }
            }

            // Execute the request
            HttpResponse response = httpClient.execute(request);

            // Get response body
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            responseTextArea.setText(responseBody);

            // Handle other response properties like status code, etc.
            // You can add more detailed response handling here if needed.

        } catch (IOException e) {
            e.printStackTrace();
            responseTextArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PostmanClone::new);
    }
}
