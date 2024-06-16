package view;

import dao.Dao;
import model.Request;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollectionsTreeMouseListener extends MouseAdapter {
    private JTree folderTree;
    private JComboBox<String> methodComboBox;
    private JTextField urlField;
    private JTextArea headersArea;
    private JTextArea requestBodyArea;

    private Dao<Request> requestDao;

    private JButton saveButton;

    private JButton editButton;

    private JButton cancelButton;

    private JButton clearButton;

    public CollectionsTreeMouseListener(JTree folderTree, JComboBox<String> methodComboBox, JTextField urlField,
                                        JTextArea headersArea, JTextArea requestBodyArea, Dao<Request> requestDao,
                                        JButton saveButton, JButton editButton, JButton cancelButton, JButton clearButton) {
        this.folderTree = folderTree;
        this.methodComboBox = methodComboBox;
        this.urlField = urlField;
        this.headersArea = headersArea;
        this.requestBodyArea = requestBodyArea;
        this.requestDao = requestDao;
        this.saveButton = saveButton;
        this.editButton = editButton;
        this.cancelButton = cancelButton;
        this.clearButton = clearButton;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            TreePath path = folderTree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (selectedNode != null && selectedNode.getParent() != null && selectedNode.getChildCount() > 0) {
                    boolean hasLeafChildren = true;
                    for (int i = 0; i < selectedNode.getChildCount(); i++) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
                        if (!childNode.isLeaf()) {
                            hasLeafChildren = false;
                            break;
                        }
                    }
                    if (hasLeafChildren) {
                        showPopupMenu(e, selectedNode);
                    } else {
                        editButton.setVisible(false);
                    }
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            TreePath path = folderTree.getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                folderTree.setSelectionPath(path);
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (selectedNode != null && selectedNode.getParent() != null && selectedNode.getChildCount() > 0) {
                    boolean hasLeafChildren = true;
                    for (int i = 0; i < selectedNode.getChildCount(); i++) {
                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
                        if (!childNode.isLeaf()) {
                            hasLeafChildren = false;
                            break;
                        }
                    }
                    if (!hasLeafChildren) {
                        folderTree.clearSelection();
                        editButton.setVisible(false);
                    }
                }
            }
        }
    }

    private void showPopupMenu(MouseEvent e, DefaultMutableTreeNode selectedNode) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem fillComponentsItem = new JMenuItem("Load");
        fillComponentsItem.addActionListener(actionEvent -> loadSelectedItem(selectedNode));
        popupMenu.add(fillComponentsItem);

        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(actionEvent -> editSelectedItem(selectedNode));
        popupMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(actionEvent -> deleteSelectedItem(selectedNode));
        popupMenu.add(deleteItem);

        popupMenu.show(folderTree, e.getX(), e.getY());
        editButton.setVisible(false);
    }

    private void editSelectedItem(DefaultMutableTreeNode selectedNode) {
        if (selectedNode != null) {
            folderTree.setSelectionPath(new TreePath(selectedNode.getPath()));
            saveButton.setVisible(false);
            editButton.setVisible(true);
            cancelButton.setVisible(true);
            clearButton.setVisible(true);

            editNode(selectedNode);
        }
    }

    private void loadSelectedItem(DefaultMutableTreeNode selectedNode) {
        if (selectedNode != null) {
            String method = null;
            String url = null;
            String headers = null;
            String body = null;
            for (int i = 0; i < selectedNode.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
                String nodeInfo = childNode.getUserObject().toString();
                if (nodeInfo.startsWith(ViewConstants.HTTP_METHOD_LABEL)) {
                    method = nodeInfo.substring(ViewConstants.HTTP_METHOD_LABEL.length()).trim();
                } else if (nodeInfo.startsWith(ViewConstants.HTTP_URL_LABEL)) {
                    url = nodeInfo.substring(ViewConstants.HTTP_URL_LABEL.length()).trim();
                } else if (nodeInfo.startsWith(ViewConstants.HTTP_HEADERS_LABEL)) {
                    headers = nodeInfo.substring(ViewConstants.HTTP_HEADERS_LABEL.length()).trim();
                } else if (nodeInfo.startsWith(ViewConstants.HTTP_BODY_LABEL)) {
                    body = nodeInfo.substring(ViewConstants.HTTP_BODY_LABEL.length()).trim();
                }
            }

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
    }

    private void deleteSelectedItem(DefaultMutableTreeNode selectedNode) {
        if (selectedNode != null) {
            String id = null;

            for (int i = 0; i < selectedNode.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
                String nodeInfo = childNode.getUserObject().toString();
                if (nodeInfo.startsWith(ViewConstants.HTTP_ID_LABEL)) {
                    id = nodeInfo.substring(ViewConstants.HTTP_ID_LABEL.length()).trim();
                }
            }

            if (id != null && !id.isEmpty()) {
                try {
                    requestDao.delete(id);

                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                    if (parentNode != null) {
                        parentNode.remove(selectedNode);
                        ((DefaultTreeModel) folderTree.getModel()).reload(parentNode);
                    } else {
                        DefaultTreeModel model = (DefaultTreeModel) folderTree.getModel();
                        model.setRoot(null);
                    }

                    JOptionPane.showMessageDialog(folderTree, "Request Deleted!");
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(folderTree, "Invalid ID format: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(folderTree, "ID not found in node", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editNode(DefaultMutableTreeNode selectedNode) {
        String requestId = null;
        for (int i = 0; i < selectedNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
            String nodeInfo = childNode.getUserObject().toString();
            if (nodeInfo.startsWith(ViewConstants.HTTP_ID_LABEL)) {
                requestId = nodeInfo.substring(ViewConstants.HTTP_ID_LABEL.length()).trim();
                break;
            }
        }

        if (requestId != null) {
            Request request = requestDao.get(requestId);
            if (request != null) {
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
}
