package view;

import javax.swing.*;
import java.awt.*;

public class UrlField extends JPanel {
    private JTextField urlField;

    public UrlField() {
        setLayout(new BorderLayout());

        urlField = new JTextField();
        add(new JLabel("URL: "), BorderLayout.WEST);
        add(urlField, BorderLayout.CENTER);
    }

    public String getUrl() {
        return urlField.getText();
    }
}
