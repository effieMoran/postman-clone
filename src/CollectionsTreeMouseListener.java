import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollectionsTreeMouseListener extends MouseAdapter {
    private JTree folderTree;
    private JComboBox<String> methodComboBox;
    private JTextField urlField;
    private JTextArea headersArea;
    private JTextArea requestBodyArea;

    public CollectionsTreeMouseListener(JTree folderTree, JComboBox<String> methodComboBox, JTextField urlField, JTextArea headersArea, JTextArea requestBodyArea) {
        this.folderTree = folderTree;
        this.methodComboBox = methodComboBox;
        this.urlField = urlField;
        this.headersArea = headersArea;
        this.requestBodyArea = requestBodyArea;
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
                }
            }
        }
    }

    private void showPopupMenu(MouseEvent e, DefaultMutableTreeNode selectedNode) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem fillComponentsItem = new JMenuItem("Fill Components with Request Details");
        fillComponentsItem.addActionListener(actionEvent -> {
            // Get the first child node (request node) under the selected collection
            //DefaultMutableTreeNode selectedNode = selectedNode;
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
        popupMenu.show(folderTree, e.getX(), e.getY());
    }
}
