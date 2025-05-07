/*
Name: Erick Hambardzumyan
Class: CS 2450
Assignment: UX Project: Daily Planner
Date: 05/07/2025
 */

package com.example.javafx_final;

// TaskController.java

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Node;

import java.io.IOException;

public class TaskController {

    @FXML private ListView<Task> taskListView; // ListView UI element to show tasks

    public static ObservableList<Task> tasks = FXCollections.observableArrayList(); // Static task list shared across views

    public TaskController() {
        instance = this; // Store singleton instance for update access
    }

    private static TaskController instance;

    // Call this to refresh the ListView with current tasks
    public static void updateTaskList() {
        if (instance != null && instance.taskListView != null) {
            instance.taskListView.setItems(null); // Clear current list (force refresh)
            instance.taskListView.setItems(tasks); // Reassign updated list
        }
    }

    public void initialize() {
        updateTaskList(); // Load tasks on initialization

        // Customize how each ListView cell displays a Task
        taskListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Task> call(ListView<Task> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Task task, boolean empty) {
                        super.updateItem(task, empty);
                        if (task == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            hbox.setStyle("-fx-padding: 6;");

                            // Colored circle indicates task priority
                            Circle colorDot = new Circle(6);
                            switch (task.getPriority()) {
                                case "High": colorDot.setFill(Color.RED); break;
                                case "Medium": colorDot.setFill(Color.GOLD); break;
                                case "Low": colorDot.setFill(Color.GREEN); break;
                                default: colorDot.setFill(Color.GRAY); break;
                            }

                            // Bold title label
                            Label titleLabel = new Label(task.getTitle());
                            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

                            // Smaller detail label for due date and priority
                            Label detailLabel = new Label("Due: " + task.getDueDate() + " | Priority: " + task.getPriority());
                            detailLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

                            VBox textBox = new VBox(titleLabel, detailLabel);
                            textBox.setSpacing(2);

                            hbox.getChildren().addAll(colorDot, textBox);
                            setGraphic(hbox); // Attach custom HBox to the cell
                        }
                    }
                };
            }
        });

        // Double-clicking a task opens edit dialog
        taskListView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                handleEditTask(null);
            }
        });
    }

    @FXML
    public void handleAddTask(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/NewTaskView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add New Task");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditTask(ActionEvent event) {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("No task selected to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/EditTaskView.fxml"));
            Parent root = loader.load();
            EditTaskController controller = loader.getController();
            controller.setTask(selected); // Load selected task into edit form
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Task");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteTask(ActionEvent event) {
        Task selected = taskListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tasks.remove(selected); // Remove selected task from list
            updateTaskList();
        } else {
            System.out.println("No task selected to delete.");
        }
    }

    @FXML
    public void handleOpenSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/SettingsView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Settings");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSwitchToCalendar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/CalendarView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Replace current scene with calendar
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Programmatically open MainView.fxml (used in other parts of app)
    public static void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(TaskController.class.getResource("MainView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Daily Planner");
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
