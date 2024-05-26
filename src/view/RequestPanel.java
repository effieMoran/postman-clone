package view;

import javax.swing.*;
import java.awt.*;

public class RequestPanel extends JPanel {
    private JTextArea headersArea;
    private JTextArea bodyArea;

    public RequestPanel() {
        setLayout(new BorderLayout());

        headersArea = new JTextArea(10, 20);
        bodyArea = new JTextArea(10, 20);

        headersArea.setBorder(BorderFactory.createTitledBorder("Headers"));
        bodyArea.setBorder(BorderFactory.createTitledBorder("Body"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(headersArea));
        splitPane.setBottomComponent(new JScrollPane(bodyArea));
        splitPane.setDividerLocation(150);

        add(splitPane, BorderLayout.CENTER);
    }

    public String getHeaders() {
        return headersArea.getText();
    }

    public String getBody() {
        return bodyArea.getText();
    }
}
