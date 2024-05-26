package view;

import javax.swing.*;
import java.awt.*;

public class BodyPanel extends JPanel {
    private JTextArea bodyArea;

    public BodyPanel() {
        setLayout(new BorderLayout());

        bodyArea = new JTextArea(10, 20);
        bodyArea.setBorder(BorderFactory.createTitledBorder("Body"));

        add(new JScrollPane(bodyArea), BorderLayout.CENTER);
    }

    public String getBody() {
        return bodyArea.getText();
    }
}
