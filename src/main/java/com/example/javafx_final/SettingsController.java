/*
Name: Erick Hambardzumyan
Class: CS 2450
Assignment: UX Project: Daily Planner
Date: 05/07/2025
 */
package com.example.javafx_final;

// SettingsController.java

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