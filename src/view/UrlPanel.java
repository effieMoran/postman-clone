package view;

import javax.swing.*;
import java.awt.*;

public class UrlPanel extends JPanel {
    private JTextField urlField;
    private JComboBox<String> methodBox;

    public UrlPanel() {
        setLayout(new BorderLayout());

        urlField = new JTextField();
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        methodBox = new JComboBox<>(methods);

        add(new JLabel("URL: "), BorderLayout.WEST);
        add(urlField, BorderLayout.CENTER);
        add(methodBox, BorderLayout.EAST);
    }

    public String getUrl() {
        return urlField.getText();
    }

    public String getMethod() {
        return (String) methodBox.getSelectedItem();
    }
}

