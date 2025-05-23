/*
Name: Erick Hambardzumyan
Class: CS 2450
Assignment: UX Project: Daily Planner
Date: 05/07/2025
 */

package com.example.javafx_final;

// Main.java

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Daily Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}