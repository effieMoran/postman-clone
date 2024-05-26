package view;

import javax.swing.*;
import java.awt.*;

public class ResponsePanel extends JPanel {
    private JTextArea responseArea;
    private JButton sendButton;

    public ResponsePanel() {
        setLayout(new BorderLayout());

        responseArea = new JTextArea(15, 20);
        responseArea.setEditable(false);
        responseArea.setBorder(BorderFactory.createTitledBorder("Response"));

        sendButton = new JButton("Send");

        add(new JScrollPane(responseArea), BorderLayout.CENTER);
        add(sendButton, BorderLayout.EAST);
    }

    public void setResponse(String response) {
        responseArea.setText(response);
    }

    public JButton getSendButton() {
        return sendButton;
    }
}
