package com.example.javafx_final;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Node;

import java.io.IOException;


public class TaskController {

    @FXML private ListView<Task> taskListView;

    public static ObservableList<Task> tasks = FXCollections.observableArrayList();

    public TaskController() {
        instance = this;
    }

    private static TaskController instance;

    public static void updateTaskList() {
        if (instance != null && instance.taskListView != null) {
            instance.taskListView.setItems(null); // Force refresh
            instance.taskListView.setItems(tasks);
        }
    }


    public void initialize() {
        updateTaskList();

        taskListView.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
            @Override
            public ListCell<Task> call(ListView<Task> listView) {
                return new ListCell<Task>() {
                    @Override
                    protected void updateItem(Task task, boolean empty) {
                        super.updateItem(task, empty);
                        if (task == null || empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Circle colorDot = new Circle(6);
                            switch (task.getPriority()) {
                                case "High": colorDot.setFill(Color.RED); break;
                                case "Medium": colorDot.setFill(Color.GOLD); break;
                                case "Low": colorDot.setFill(Color.GREEN); break;
                                default: colorDot.setFill(Color.GRAY); break;
                            }
                            Label label = new Label(task.getTitle());
                            hbox.getChildren().addAll(colorDot, label);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        // Allow double-click to edit
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
            controller.setTask(selected);
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
            tasks.remove(selected);
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
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(TaskController.class.getResource("MainView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Daily Planner");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current calendar view window
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
