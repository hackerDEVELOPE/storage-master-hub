package com.example.cloudapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextArea textArea;

    private StorageController controller;


    public void setController(StorageController controller) {
        this.controller = controller;
    }

    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        controller.registration(login, password);
    }
}
