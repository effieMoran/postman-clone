package view;

import javax.swing.*;
import java.awt.*;

public class HeadersPanel extends JPanel {
    private JTextArea headersArea;

    public HeadersPanel() {
        setLayout(new BorderLayout());

        headersArea = new JTextArea(10, 20);
        headersArea.setBorder(BorderFactory.createTitledBorder("Headers"));

        add(new JScrollPane(headersArea), BorderLayout.CENTER);
    }

    public String getHeaders() {
        return headersArea.getText();
    }
}
