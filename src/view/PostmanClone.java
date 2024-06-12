package view;

import config.DataBaseConfiguration;
import dao.Dao;
import dao.MongoRequestDao;
import helpers.HTTPService;
import helpers.ResponseFormatter;
import model.Request;
import org.apache.http.client.methods.HttpRequestBase;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
    private JTree folderTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private Map<String, DefaultMutableTreeNode> folderNodeMap;

    private HTTPService httpService;
    private Dao<Request> requestDao;
    private DataBaseConfiguration dbConfig;

    public PostmanClone() {
        httpService = new HTTPService();
        dbConfig = new DataBaseConfiguration();
        requestDao = new MongoRequestDao(dbConfig);
        folderNodeMap = new HashMap<>();  // Initialize folderNodeMap

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

        root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
        folderTree = new JTree(treeModel);

        CollectionsTreeMouseListener mouseListener = new CollectionsTreeMouseListener(folderTree, methodComboBox, urlField, headersArea, requestBodyArea);
        folderTree.addMouseListener(mouseListener);

        JScrollPane folderScrollPane = new JScrollPane(folderTree);
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

    //todo: ClientProtocolException handle this

    private void sendRequest() {
        String url = urlField.getText();
        String method = (String) methodComboBox.getSelectedItem();
        String headers = headersArea.getText();
        String requestBody = requestBodyArea.getText();
        try {
            HttpRequestBase request = httpService.createRequest(method, url, requestBody);
            httpService.addHeaders(request, headers);
            String responseBody = httpService.executeRequest(request);

            // Format the response based on content type
            String formattedResponse = ResponseFormatter.format(responseBody);

            // Update the response text area with the formatted response
            responseTextArea.setText(formattedResponse);
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

        // Get the list of existing folders
        String[] folderNames = folderNodeMap.keySet().toArray(new String[0]);

        // Create the JComboBox for folder selection
        JComboBox<String> collectionsComboBox = new JComboBox<>(folderNames);
        collectionsComboBox.setEditable(true); // Allow new folder creation

        // Show the dialog
        int result = JOptionPane.showConfirmDialog(this, collectionsComboBox, "Select or Create Collection", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedFolder = (String) collectionsComboBox.getSelectedItem();

            if (selectedFolder != null && !selectedFolder.trim().isEmpty()) {
                // Save the request with the selected folder name
                Request request = new Request(method, url, headers, body, selectedFolder);
                requestDao.save(request);

                // Update UI and show message
                updateCollectionsTree(selectedFolder, request);
                JOptionPane.showMessageDialog(this, "model.Request Saved!");
            }
        }
    }

    private DefaultMutableTreeNode addCollection(String folderName) {
        DefaultMutableTreeNode folderNode = folderNodeMap.get(folderName);
        if (folderNode == null) {
            folderNode = new DefaultMutableTreeNode(folderName);
            root.add(folderNode);
            folderNodeMap.put(folderName, folderNode);
            treeModel.reload(root); // Reload the root node to update the tree
        }
        return folderNode;
    }

    private void addCollection() {
        String collectionName = JOptionPane.showInputDialog(this, "Enter Collection Name:");
        if (collectionName != null && !collectionName.isEmpty()) {
            addCollection(collectionName);
        }
    }

    private void updateCollectionsTree(String folderName, Request request) {
        DefaultMutableTreeNode folderNode = folderNodeMap.get(folderName);
        if (folderNode == null) {
            folderNode = addCollection(folderName);
        }

        DefaultMutableTreeNode requestNode = createRequestNode(request);

        folderNode.add(requestNode);

        treeModel.reload(folderNode);
    }

    private void loadSavedRequests() {
        addCollection("Recent"); // Add the default "Recent" folder

        List<Request> requests = requestDao.getAll();
        for (Request request : requests) {
            String folderName = request.getFolder() != null && !request.getFolder().isEmpty() ? request.getFolder() : "Recent";
            DefaultMutableTreeNode folderNode = addCollection(folderName);

            DefaultMutableTreeNode requestNode = createRequestNode(request);

            folderNode.add(requestNode);

            treeModel.reload(folderNode);
        }
    }

    private DefaultMutableTreeNode createRequestNode(Request request) {
        // Create the main node for the request
        DefaultMutableTreeNode requestNode = new DefaultMutableTreeNode(request.getMethod() + ": " + request.getUrl());

        // Create child nodes for URL, headers, and body
        DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode("Method: " + request.getMethod());
        DefaultMutableTreeNode urlNode = new DefaultMutableTreeNode("URL: " + request.getUrl());
        DefaultMutableTreeNode headersNode = new DefaultMutableTreeNode("Headers: " + request.getHeaders());
        DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode("Body: " + request.getBody());

        // Add child nodes to the main request node
        requestNode.add(methodNode);
        requestNode.add(urlNode);
        requestNode.add(headersNode);
        requestNode.add(bodyNode);

        return requestNode;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PostmanClone::new);
    }
}
