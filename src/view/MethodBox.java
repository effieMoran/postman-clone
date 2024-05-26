package view;

import javax.swing.*;
import java.awt.*;

public class MethodBox extends JPanel {
    private JComboBox<String> methodBox;

    public MethodBox() {
        setLayout(new BorderLayout());

        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        methodBox = new JComboBox<>(methods);
        add(methodBox, BorderLayout.CENTER);
    }

    public String getMethod() {
        return (String) methodBox.getSelectedItem();
    }
}
