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

        UrlField urlField = new UrlField();
        MethodBox methodBox = new MethodBox();
        ResponseArea responseArea = new ResponseArea();
        SendButton sendButton = new SendButton();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(urlField, BorderLayout.CENTER);
        topPanel.add(methodBox, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS)); // Stack components vertically

        BodyPanel bodyPanel = new BodyPanel();
        HeadersPanel headersPanel = new HeadersPanel();
        middlePanel.add(headersPanel);
        middlePanel.add(bodyPanel);

        mainPanel.add(middlePanel, BorderLayout.CENTER);

        JPanel downPanel = new JPanel(new BorderLayout());
        sendButton.setPreferredSize(new Dimension(100, sendButton.getPreferredSize().height));
        downPanel.add(responseArea, BorderLayout.CENTER);
        downPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(downPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

}
