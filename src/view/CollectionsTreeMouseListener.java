package view;

import dao.Dao;
import model.Request;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class CollectionsTreeMouseListener extends MouseAdapter {
    private JTree folderTree;
    private JComboBox<String> methodComboBox;
    private JTextField urlField;
    private JTextArea headersArea;
    private JTextArea requestBodyArea;

    private Dao<Request> requestDao;

    private JButton saveButton;

    private JButton editButton;
    public CollectionsTreeMouseListener(JTree folderTree, JComboBox<String> methodComboBox, JTextField urlField,
                                        JTextArea headersArea, JTextArea requestBodyArea, Dao<Request> requestDao
                                        , JButton saveButton, JButton editButton) {
        this.folderTree = folderTree;
        this.methodComboBox = methodComboBox;
        this.urlField = urlField;
        this.headersArea = headersArea;
        this.requestBodyArea = requestBodyArea;
        this.requestDao = requestDao;
        this.saveButton = saveButton;
        this.editButton = editButton;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            // Get the path to the node that was right-clicked
            TreePath path = folderTree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (selectedNode != null && !selectedNode.isLeaf()) { // Check if it's a collection node
                    // Display a popup menu with the option to fill the components
                    showPopupMenu(e, selectedNode);

                    // Show the edit button when a collection node is selected
                    editButton.setVisible(true);
                }
            }
        }
    }

    private void showPopupMenu(MouseEvent e, DefaultMutableTreeNode selectedNode) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem fillComponentsItem = new JMenuItem("Load");
        fillComponentsItem.addActionListener(actionEvent -> {
            if (selectedNode != null) {
                // Retrieve request details from the child node
                String method = null;
                String url = null;
                String headers = null;
                String body = null;
                for (int i = 0; i < selectedNode.getChildCount(); i++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
                    String nodeInfo = childNode.getUserObject().toString();
                    if (nodeInfo.startsWith("Method: ")) {
                        method = nodeInfo.substring("Method: ".length()).trim();
                    } else if (nodeInfo.startsWith("URL: ")) {
                        url = nodeInfo.substring("URL: ".length()).trim();
                    } else if (nodeInfo.startsWith("Headers: ")) {
                        headers = nodeInfo.substring("Headers: ".length()).trim();
                    } else if (nodeInfo.startsWith("Body: ")) {
                        body = nodeInfo.substring("Body: ".length()).trim();
                    }
                }

                // Populate UI components with retrieved request details
                if (method != null) {
                    methodComboBox.setSelectedItem(method);
                }
                if (url != null) {
                    urlField.setText(url);
                }
                if (headers != null) {
                    headersArea.setText(headers);
                }
                if (body != null) {
                    requestBodyArea.setText(body);
                }
            }
        });
        popupMenu.add(fillComponentsItem);

        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(actionEvent -> {
            if (selectedNode != null) {
                // When the Edit menu option is clicked, hide the save button and show the edit button
                saveButton.setVisible(false);
                editButton.setVisible(true);

                // Call the editNode method to update the content
                editNode(selectedNode);
            }
        });
        popupMenu.add(editItem);

        popupMenu.add(editItem);
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(actionEvent -> {
            if (selectedNode != null) {
                String id = null;

                for (int i = 0; i < selectedNode.getChildCount(); i++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
                    String nodeInfo = childNode.getUserObject().toString();
                    if (nodeInfo.startsWith("Id: ")) {
                        id = nodeInfo.substring("Id: ".length()).trim();
                    }
                }

                if (id != null && !id.isEmpty()) {
                    try {
                        // Delete the request from the database
                        requestDao.delete(id);

                        // Remove the node from the tree
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                        if (parentNode != null) {
                            parentNode.remove(selectedNode);
                            ((DefaultTreeModel) folderTree.getModel()).reload(parentNode);
                        } else {
                            DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
                            model.setRoot(null); // or model.removeNodeFromParent(selectedNode);
                        }

                        JOptionPane.showMessageDialog(folderTree, "Request Deleted!");
                    } catch (NumberFormatException e1) {
                        // Handle parsing exception (e.g., invalid ID format)
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(folderTree, "Invalid ID format: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(folderTree, "ID not found in node", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        popupMenu.add(deleteItem);

        popupMenu.show(folderTree, e.getX(), e.getY());
    }

    private void editNode(DefaultMutableTreeNode selectedNode) {
        // Retrieve the ID of the request from the node
        String requestId = null;
        for (int i = 0; i < selectedNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
            String nodeInfo = childNode.getUserObject().toString();
            if (nodeInfo.startsWith("Id: ")) {
                requestId = nodeInfo.substring("Id: ".length()).trim();
                break;  // Exit loop once ID is found
            }
        }

        if (requestId != null) {
            Request request = requestDao.get(requestId);
            if (request != null) {
                // Populate UI components with the current request details
                methodComboBox.setSelectedItem(request.getMethod());
                urlField.setText(request.getUrl());
                headersArea.setText(request.getHeaders());
                requestBodyArea.setText(request.getBody());
            } else {
                JOptionPane.showMessageDialog(folderTree, "Request not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(folderTree, "ID not found in node", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Method to clear all UI fields
    private void clearFields() {
        methodComboBox.setSelectedIndex(0);
        urlField.setText("");
        headersArea.setText("");
        requestBodyArea.setText("");
        // Hide the edit button
        editButton.setVisible(false);
    }

    // Method to handle editing the request
    private void handleEdit() {
        // Hide the edit button
        editButton.setVisible(false);
        // Display the save button
        saveButton.setVisible(true);
    }



}
