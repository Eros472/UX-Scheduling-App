// File: SettingsController.java
package com.example.javafx_final;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class SettingsController {

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void handleSave(ActionEvent event) {
        // Settings save logic can go here later if needed
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }
}