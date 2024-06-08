import org.apache.http.client.methods.HttpRequestBase;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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
    private JTree folderTree; // Changed to JTree
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private Map<String, DefaultMutableTreeNode> folderNodeMap;

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

        String selectedFolder = getSelectedFolder();
        RequestDAO.Request request = new RequestDAO.Request(method, url, headers, body);

        // Set folder to "Recents" if not selected
        if (selectedFolder == null) {
            selectedFolder = "Recents";
        } else {
            // Clear selection to ensure the "Recents" folder is selected in UI
            folderTree.clearSelection();
        }

        requestDAO.saveRequest(request);

        // Update UI and show message
        updateFolderTree(selectedFolder, request);
        JOptionPane.showMessageDialog(this, "Request Saved!");
    }

    private void updateFolderTree(String folderName, RequestDAO.Request request) {
        DefaultMutableTreeNode folderNode = folderNodeMap.get(folderName);
        if (folderNode != null) {
            DefaultListModel<RequestDAO.Request> requestListModel = folderRequestMap.get(folderName);
            if (requestListModel != null && request != null) {
                requestListModel.addElement(request);
            }
            treeModel.reload(folderNode);
        }
    }

    private String getSelectedFolder() {
        TreePath path = folderTree.getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            return selectedNode.getUserObject().toString();
        }
        return null;
    }
    private void addFolder(String folderName) {
        if (root == null) {
            root = new DefaultMutableTreeNode("Root");
            treeModel = new DefaultTreeModel(root);
            folderNodeMap = new HashMap<>();
            folderTree = new JTree(treeModel); // Initialize folderTree
            collectionsPanel.add(new JScrollPane(folderTree), BorderLayout.CENTER); // Add folderTree to the collectionsPanel
        }

        DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folderName);
        root.add(folderNode);
        folderNodeMap.put(folderName, folderNode);
        DefaultListModel<RequestDAO.Request> requestListModel = new DefaultListModel<>();
        folderRequestMap.put(folderName, requestListModel);
        treeModel.reload();
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
            DefaultMutableTreeNode folderNode = root; // Default to root folder
            String folderName = "Recents"; // Default folder name

            // If the request has a specific folder assigned, use it
            if (request.getFolder() != null && !request.getFolder().isEmpty()) {
                folderName = request.getFolder();
                folderNode = folderNodeMap.get(folderName);
                if (folderNode == null) {
                    // If the folder doesn't exist, create it
                    addFolder(folderName);
                    folderNode = folderNodeMap.get(folderName);
                }
            }

            // Create a node for the request
            DefaultMutableTreeNode requestNode = new DefaultMutableTreeNode(request.getMethod() + ": " + request.getUrl());
            folderNode.add(requestNode);

            // Update the tree model
            treeModel.reload(folderNode);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PostmanClone::new);
    }
}
