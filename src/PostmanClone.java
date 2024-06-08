import org.apache.http.client.methods.HttpRequestBase;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostmanClone extends JFrame {
    private JTextField urlField;
    private JComboBox<String> methodComboBox;
    private JTextArea headersArea;
    private JTextArea requestBodyArea;
    private JTextArea responseTextArea;
    private JButton sendButton;
    private JButton saveButton;
    private JButton addCollectionButton;
    private JPanel collectionsPanel;
    private JList<String> folderList;
    private DefaultListModel<String> folderListModel;
    private Map<String, DefaultListModel<RequestDAO.Request>> folderRequestMap;

    private HTTPService httpService;
    private RequestDAO requestDAO;

    public PostmanClone() {
        httpService = new HTTPService();
        requestDAO = new RequestDAO();
        folderRequestMap = new HashMap<>();

        setTitle("Postman Clone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);

        initComponents();
        addComponentsToFrame();
        loadSavedRequests();

        setVisible(true);
    }

    private void initComponents() {
        urlField = new JTextField("http://google.com", 40);
        methodComboBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        headersArea = new JTextArea(5, 30);
        requestBodyArea = new JTextArea(10, 30);
        responseTextArea = new JTextArea(15, 50);
        responseTextArea.setEditable(false);
        sendButton = new JButton("Send");
        saveButton = new JButton("Save");
        addCollectionButton = new JButton("Add Collection");
        sendButton.addActionListener(e -> sendRequest());
        saveButton.addActionListener(e -> saveRequest());
        addCollectionButton.addActionListener(e -> addCollection());

        collectionsPanel = new JPanel(new BorderLayout());

        folderListModel = new DefaultListModel<>();
        folderList = new JList<>(folderListModel);
        folderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        folderList.addListSelectionListener(e -> {
            String selectedFolder = folderList.getSelectedValue();
            if (selectedFolder != null) {
                JList<RequestDAO.Request> requestList = new JList<>(folderRequestMap.get(selectedFolder));
                JScrollPane scrollPane = new JScrollPane(requestList);
                collectionsPanel.add(scrollPane, BorderLayout.CENTER);
                collectionsPanel.revalidate();
                collectionsPanel.repaint();
            }
        });

        JScrollPane folderScrollPane = new JScrollPane(folderList);
        collectionsPanel.add(addCollectionButton, BorderLayout.NORTH);
        collectionsPanel.add(folderScrollPane, BorderLayout.CENTER);
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

        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel headersPanel = new JPanel(new BorderLayout());
        headersPanel.add(new JLabel("Headers:"), BorderLayout.NORTH);
        headersPanel.add(new JScrollPane(headersArea), BorderLayout.CENTER);

        JPanel requestBodyPanel = new JPanel(new BorderLayout());
        requestBodyPanel.add(new JLabel("Body:"), BorderLayout.NORTH);
        requestBodyPanel.add(new JScrollPane(requestBodyArea), BorderLayout.CENTER);

        JPanel requestPanel = new JPanel(new GridLayout(2, 1));
        requestPanel.add(headersPanel);
        requestPanel.add(requestBodyPanel);

        centerPanel.add(requestPanel, BorderLayout.NORTH);

        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.add(new JLabel("Response:"), BorderLayout.NORTH);
        responsePanel.add(new JScrollPane(responseTextArea), BorderLayout.CENTER);

        centerPanel.add(responsePanel, BorderLayout.CENTER);

        container.add(centerPanel, BorderLayout.CENTER);

        container.add(collectionsPanel, BorderLayout.WEST);
    }

    private void sendRequest() {
        String url = urlField.getText();
        String method = (String) methodComboBox.getSelectedItem();
        String headers = headersArea.getText();
        String requestBody = requestBodyArea.getText();

        try {
            HttpRequestBase request = httpService.createRequest(method, url, requestBody);
            httpService.addHeaders(request, headers);
            String responseBody = httpService.executeRequest(request);
            responseTextArea.setText(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            responseTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void saveRequest() {
        String method = (String) methodComboBox.getSelectedItem();
        String url = urlField.getText();
        String headers = headersArea.getText();
        String body = requestBodyArea.getText();

        RequestDAO.Request request = new RequestDAO.Request(method, url, headers, body);
        requestDAO.saveRequest(request);

        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null) {
            DefaultListModel<RequestDAO.Request> requestListModel = folderRequestMap.get(selectedFolder);
            if (requestListModel != null) {
                requestListModel.addElement(request);
            }
        }
        JOptionPane.showMessageDialog(this, "Request Saved!");
    }

    private void addFolder(String folderName) {
        DefaultListModel<RequestDAO.Request> requestListModel = new DefaultListModel<>();
        folderRequestMap.put(folderName, requestListModel);
        folderListModel.addElement(folderName);
    }

    private void addCollection() {
        String collectionName = JOptionPane.showInputDialog(this, "Enter Collection Name:");
        if (collectionName != null && !collectionName.isEmpty()) {
            addFolder(collectionName);
        }
    }

    private void loadSavedRequests() {
        addFolder("Recents"); // Add the default "Recents" folder

        List<RequestDAO.Request> requests = requestDAO.getAllRequests();
        for (RequestDAO.Request request : requests) {
            DefaultListModel<RequestDAO.Request> recentRequestsModel = folderRequestMap.get("Recents");
            if (recentRequestsModel != null) {
                recentRequestsModel.addElement(request);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PostmanClone::new);
    }
}
