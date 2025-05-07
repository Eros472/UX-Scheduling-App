// CalendarController.java (FINAL - With Add Task Prefill + Centered Header)
package com.example.javafx_final;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarController {
    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;
    @FXML private HBox monthHeaderBox;

    private LocalDate currentMonth = LocalDate.now();

    @FXML
    public void initialize() {
        setupResponsiveGrid();
        setupTaskSyncListener();
        updateCalendar();
    }

    private void setupResponsiveGrid() {
        calendarGrid.getColumnConstraints().clear();
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            col.setHalignment(javafx.geometry.HPos.CENTER);
            calendarGrid.getColumnConstraints().add(col);
        }

        calendarGrid.getRowConstraints().clear();
        for (int i = 0; i < 7; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 7);
            row.setValignment(javafx.geometry.VPos.TOP);
            calendarGrid.getRowConstraints().add(row);
        }
    }

    private void setupTaskSyncListener() {
        TaskController.tasks.addListener((ListChangeListener<Task>) change -> updateCalendar());
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
        populateCalendar(currentMonth);
    }

    private void populateCalendar(LocalDate monthStart) {
        calendarGrid.getChildren().clear();

        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayHeader = new Label(weekdays[i]);
            dayHeader.setStyle("-fx-font-weight: bold;");
            StackPane headerPane = new StackPane(dayHeader);
            headerPane.setPrefHeight(40);
            calendarGrid.add(headerPane, i, 0);
        }

        LocalDate date = monthStart.withDayOfMonth(1);
        int startDayOfWeek = date.getDayOfWeek().getValue() % 7;
        int daysInMonth = date.lengthOfMonth();

        Map<String, List<Task>> tasksByDate = TaskController.tasks.stream()
                .collect(Collectors.groupingBy(task -> {
                    String fullDate = task.getDueDate();
                    return fullDate != null && fullDate.length() >= 10 ? fullDate.substring(0, 10) : "UNKNOWN";
                }));

        int row = 1;
        int col = startDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            VBox dayBox = new VBox(4);
            dayBox.setPadding(new Insets(5));
            dayBox.setPrefSize(110, 90);
            dayBox.setAlignment(Pos.TOP_CENTER);
            dayBox.setStyle("-fx-border-color: #DDD; -fx-border-width: 1;");

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            dayBox.getChildren().add(dayLabel);

            LocalDate thisDay = date.withDayOfMonth(day);
            if (thisDay.equals(LocalDate.now())) {
                dayBox.setStyle("-fx-background-color: #e0f7fa; -fx-border-color: #2196f3; -fx-border-width: 2;");
            }

            String dateKey = thisDay.toString();
            List<Task> dayTasks = tasksByDate.getOrDefault(dateKey, new ArrayList<>());

            if (!dayTasks.isEmpty()) {
                Label taskCount = new Label("+" + dayTasks.size() + " task" + (dayTasks.size() > 1 ? "s" : ""));
                taskCount.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
                dayBox.getChildren().add(taskCount);
            }

            calendarGrid.add(dayBox, col, row);

            dayBox.setOnMouseClicked(event -> showTasksForDate(thisDay));

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void showTasksForDate(LocalDate date) {
        List<Task> tasks = TaskController.tasks.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().startsWith(date.toString()))
                .collect(Collectors.toList());

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Tasks for " + date);
        dialog.setHeaderText("Click a task to edit it or add a new task");

        VBox taskList = new VBox(10);
        taskList.setPadding(new Insets(10));

        for (Task task : tasks) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Button taskButton = new Button(task.getTitle());
            taskButton.setMaxWidth(Double.MAX_VALUE);
            taskButton.setOnAction(e -> {
                dialog.setResult(task);
                dialog.close();
            });
            HBox.setHgrow(taskButton, Priority.ALWAYS);

            Button deleteButton = new Button("🗑");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
            deleteButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Delete Task");
                alert.setContentText("Are you sure you want to delete this task?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    TaskController.tasks.remove(task);
                    updateCalendar();
                    taskList.getChildren().remove(row);
                }
            });

            row.getChildren().addAll(taskButton, deleteButton);
            taskList.getChildren().add(row);
        }

        Button addButton = new Button("+ Add New Task");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setStyle("-fx-background-color: #dddddd; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/NewTaskView.fxml"));
                Parent root = loader.load();
                NewTaskController controller = loader.getController();
                controller.prefillDate(date);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Add Task on " + date);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        taskList.getChildren().add(new Separator());
        taskList.getChildren().add(addButton);

        dialog.getDialogPane().setContent(taskList);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(selectedTask -> {
            if (selectedTask != null && !TaskController.tasks.contains(selectedTask)) {
                TaskController.tasks.removeIf(t -> t.getTitle().equals(selectedTask.getTitle()) && t.getDueDate().equals(selectedTask.getDueDate()));
            }
            updateCalendar();
            try {
                FXMLLoader loader;
                Parent root;
                Stage stage = new Stage();
                if (selectedTask == null) {
                    loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/NewTaskView.fxml"));
                    root = loader.load();
                    NewTaskController controller = loader.getController();
                    controller.prefillDate(date);
                    stage.setTitle("Add Task on " + date);
                } else {
                    loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/EditTaskView.fxml"));
                    root = loader.load();
                    EditTaskController controller = loader.getController();
                    controller.setTask(selectedTask);
                    stage.setTitle("Edit Task");
                }
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void handlePrevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    public void handleNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }

    @FXML
    public void handleBackToPlanner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Daily Planner");
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) calendarGrid.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOpenSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
