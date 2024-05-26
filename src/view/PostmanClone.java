package view;

import javax.swing.*;
import java.awt.*;

public class PostmanClone extends JFrame {

    public PostmanClone() {
        setTitle("Postman Clone");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        UrlPanel urlPanel = new UrlPanel();
        RequestPanel requestPanel = new RequestPanel();
        ResponsePanel responsePanel = new ResponsePanel();

        mainPanel.add(urlPanel, BorderLayout.NORTH);
        mainPanel.add(requestPanel, BorderLayout.CENTER);
        mainPanel.add(responsePanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


}
