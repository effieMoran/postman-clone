package view;

import javax.swing.*;
import java.awt.*;

public class ResponseArea extends JPanel {
    private JTextArea responseArea;

    public ResponseArea() {
        setLayout(new BorderLayout());

        responseArea = new JTextArea(15, 20);
        responseArea.setEditable(false);
        responseArea.setBorder(BorderFactory.createTitledBorder("Response"));

        add(new JScrollPane(responseArea), BorderLayout.CENTER);
    }

    public void setResponse(String response) {
        responseArea.setText(response);
    }

    public JTextArea getResponseArea() {
        return responseArea;
    }
}
