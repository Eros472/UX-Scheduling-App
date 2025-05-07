// File: CalendarController.java
package com.example.javafx_final;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarController {
    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;

    private LocalDate currentMonth = LocalDate.now();

    @FXML
    public void initialize() {
        setupResponsiveGrid();
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

    private void updateCalendar() {
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
        populateCalendar(currentMonth);
    }

    private void populateCalendar(LocalDate monthStart) {
        calendarGrid.getChildren().clear();

        // Add weekday headers
        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayHeader = new Label(weekdays[i]);
            dayHeader.setStyle("-fx-font-weight: bold;");
            StackPane headerPane = new StackPane(dayHeader);
            headerPane.setPrefHeight(40);
            calendarGrid.add(headerPane, i, 0);
        }

        LocalDate date = monthStart.withDayOfMonth(1);
        int startDayOfWeek = date.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = date.lengthOfMonth();

        Map<String, List<Task>> tasksByDate = TaskController.tasks.stream()
                .collect(Collectors.groupingBy(Task::getDueDate));

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

            String dateKey = date.withDayOfMonth(day).toString();
            List<Task> dayTasks = tasksByDate.getOrDefault(dateKey, new ArrayList<>());

            FlowPane taskDots = new FlowPane();
            taskDots.setHgap(3);
            taskDots.setVgap(3);
            taskDots.setPrefWrapLength(100);
            taskDots.setAlignment(Pos.TOP_CENTER);
            for (Task task : dayTasks) {
                Circle dot = new Circle(4);
                switch (task.getPriority()) {
                    case "High": dot.setFill(Color.RED); break;
                    case "Medium": dot.setFill(Color.GOLD); break;
                    case "Low": dot.setFill(Color.GREEN); break;
                    default: dot.setFill(Color.GRAY);
                }
                taskDots.getChildren().add(dot);
            }

            dayBox.getChildren().add(taskDots);
            calendarGrid.add(dayBox, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
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
