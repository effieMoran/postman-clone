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
    private JButton saveButton;
    private JPanel collectionsPanel;

    private HttpClient httpClient;

    private HttpRequestFactory httpRequestFactory;


    public PostmanClone() {
        httpClient = HttpClients.createDefault();
        httpRequestFactory = new HttpRequestFactory();

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
        saveButton = new JButton("Save");
        sendButton.addActionListener(e -> sendRequest());

        // Initialize collections panel
        collectionsPanel = new JPanel(new BorderLayout());
        JButton addCollectionButton = new JButton("+");
        addCollectionButton.addActionListener(e -> addCollection());
        collectionsPanel.add(addCollectionButton, BorderLayout.NORTH);
    }

    private void addComponentsToFrame() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Method:"));
        topPanel.add(methodComboBox);
        topPanel.add(new JLabel("URL:"));
        topPanel.add(urlField);
        topPanel.add(sendButton);
        topPanel.add(saveButton);
        container.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.add(new JScrollPane(headersArea));
        centerPanel.add(new JScrollPane(requestBodyArea));
        centerPanel.add(new JScrollPane(responseTextArea));
        container.add(centerPanel, BorderLayout.CENTER);

        container.add(collectionsPanel, BorderLayout.WEST);
    }

    private void sendRequest() {
        String url = urlField.getText();
        String method = (String) methodComboBox.getSelectedItem();
        String headers = headersArea.getText();
        String requestBody = requestBodyArea.getText();

        try {
            HttpRequestBase request = httpRequestFactory.createRequest(method, url, requestBody);
            httpRequestFactory.addHeaders(request, headers);
            String responseBody = httpRequestFactory.executeRequest(request);
            responseTextArea.setText(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            responseTextArea.setText("Error: " + e.getMessage());
        }
    }
    private void addCollection() {
        // Placeholder method for adding collections
        JOptionPane.showMessageDialog(this, "Collection Added!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PostmanClone::new);
    }
}
