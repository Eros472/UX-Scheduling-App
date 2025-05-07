/*
Name: Erick Hambardzumyan
Class: CS 2450
Assignment: UX Project: Daily Planner
Date: 05/07/2025
 */

package com.example.javafx_final;

// CalendarController.java

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
import javafx.scene.shape.Circle;
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
        // Setup layout and listeners when calendar screen loads
        setupResponsiveGrid();
        setupTaskSyncListener();
        updateCalendar();
    }

    private void setupResponsiveGrid() {
        // Set 7 equal-width columns
        calendarGrid.getColumnConstraints().clear();
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            col.setHalignment(javafx.geometry.HPos.CENTER);
            calendarGrid.getColumnConstraints().add(col);
        }

        // Set 7 equal-height rows
        calendarGrid.getRowConstraints().clear();
        for (int i = 0; i < 7; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 7);
            row.setValignment(javafx.geometry.VPos.TOP);
            calendarGrid.getRowConstraints().add(row);
        }
    }

    private void setupTaskSyncListener() {
        // Redraw calendar whenever task list changes
        TaskController.tasks.addListener((ListChangeListener<Task>) change -> updateCalendar());
    }

    private void updateCalendar() {
        // Update the month label and regenerate the calendar grid
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
        populateCalendar(currentMonth);
    }

    private void populateCalendar(LocalDate monthStart) {
        calendarGrid.getChildren().clear();

        // Add weekday headers (Sun-Sat)
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

        int row = 1;
        int col = startDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            VBox dayBox = new VBox(4);
            dayBox.setPadding(new Insets(5));
            dayBox.setPrefSize(110, 90);
            dayBox.setAlignment(Pos.TOP_CENTER);
            dayBox.setStyle("-fx-border-color: #e0e0cc; -fx-border-width: 1;");

            // Add numeric day label
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            dayBox.getChildren().add(dayLabel);

            // Highlight current day
            LocalDate thisDay = date.withDayOfMonth(day);
            if (thisDay.equals(LocalDate.now())) {
                dayBox.setStyle("-fx-background-color: #fffbe6; -fx-border-color: #bfa100; -fx-border-width: 2;");
            }

            // Add priority dots for tasks on that day
            Map<String, Long> priorityCounts = TaskController.tasks.stream()
                    .filter(task -> {
                        try {
                            LocalDate due = LocalDate.parse(task.getDueDate().substring(0, 10));
                            return due.equals(thisDay);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

            if (!priorityCounts.isEmpty()) {
                VBox dotsBox = new VBox(3);
                dotsBox.setAlignment(Pos.CENTER);

                priorityCounts.forEach((priority, count) -> {
                    HBox dotRow = new HBox(4);
                    dotRow.setAlignment(Pos.CENTER);
                    Circle dot = new Circle(4);
                    switch (priority) {
                        case "High": dot.setFill(Color.RED); break;
                        case "Medium": dot.setFill(Color.GOLD); break;
                        case "Low": dot.setFill(Color.GREEN); break;
                        default: dot.setFill(Color.GRAY);
                    }
                    Label countLabel = new Label("+" + count);
                    dotRow.getChildren().addAll(dot, countLabel);
                    dotsBox.getChildren().add(dotRow);
                });

                dayBox.getChildren().add(dotsBox);
            }

            // Show expanded weekly view when clicking a day
            calendarGrid.add(dayBox, col, row);
            dayBox.setOnMouseClicked(event -> showWeeklyExpandedView(thisDay));

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void showWeeklyExpandedView(LocalDate selectedDate) {
        // Open popup window to show 7-day task details
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Weekly View");
        dialogStage.setWidth(1000);
        dialogStage.setHeight(600);

        VBox wrapper = new VBox(15);
        wrapper.setPadding(new Insets(20));
        wrapper.setStyle("-fx-background-color: #f9f9f9;");

        // Determine Sunday start of selected week
        LocalDate weekStart = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);

        // Top header with navigation
        Button prevWeek = new Button("←");
        Button nextWeek = new Button("→");
        Label header = new Label("Tasks for the week of " + weekStart);
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox nav = new HBox(15, prevWeek, header, nextWeek);
        nav.setAlignment(Pos.CENTER);
        wrapper.getChildren().add(nav);

        // Allow navigating to previous or next week
        prevWeek.setOnAction(e -> {
            dialogStage.close();
            showWeeklyExpandedView(weekStart.minusWeeks(1));
        });
        nextWeek.setOnAction(e -> {
            dialogStage.close();
            showWeeklyExpandedView(weekStart.plusWeeks(1));
        });

        HBox weekRow = new HBox(15);
        weekRow.setAlignment(Pos.TOP_CENTER);

        // Populate each day of the week with task cards
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            VBox dayColumn = new VBox(8);
            dayColumn.setPadding(new Insets(10));
            dayColumn.setStyle("-fx-border-color: #ccc; -fx-background-color: #ffffff; -fx-border-radius: 5px;");
            dayColumn.setPrefWidth(120);

            // Display day name and number
            Label dateLabel = new Label(day.getDayOfWeek().toString().substring(0, 3) + "\n" + day.getDayOfMonth());
            dateLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
            dayColumn.getChildren().add(dateLabel);

            // Show each task for the current day
            List<Task> dayTasks = TaskController.tasks.stream()
                    .filter(task -> {
                        try {
                            LocalDate due = LocalDate.parse(task.getDueDate().substring(0, 10));
                            return due.equals(day);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            for (Task task : dayTasks) {
                VBox card = new VBox(4);
                card.setPadding(new Insets(6));
                card.setSpacing(2);
                card.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-text-fill: white;");

                // Choose background color based on task priority
                String color = switch (task.getPriority()) {
                    case "High" -> "#d32f2f";
                    case "Medium" -> "#fbc02d";
                    case "Low" -> "#388e3c";
                    default -> "gray";
                };
                card.setStyle(card.getStyle() + " -fx-background-color: " + color + ";");

                Label title = new Label(task.getTitle());
                title.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

                Label desc = new Label(task.getDescription());
                desc.setWrapText(true);
                desc.setStyle("-fx-font-size: 11; -fx-text-fill: white;");

                String timePart = task.getDueDate().length() > 10 ? task.getDueDate().substring(11) : "";
                Label time = new Label(timePart);
                time.setStyle("-fx-font-size: 10; -fx-font-style: italic; -fx-text-fill: white;");

                // Indicate overdue tasks visually
                if (LocalDate.parse(task.getDueDate().substring(0, 10)).isBefore(LocalDate.now())) {
                    Label pastDue = new Label("PAST DUE!");
                    pastDue.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #b71c1c; -fx-padding: 2 4 2 4; -fx-background-radius: 4;");
                    card.getChildren().add(pastDue);
                }

                card.getChildren().addAll(title, desc, time);
                dayColumn.getChildren().add(card);
            }

            // Add task button for this day
            Button addButton = new Button("+ Add");
            addButton.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_final/NewTaskView.fxml"));
                    Parent root = loader.load();
                    NewTaskController controller = loader.getController();
                    controller.prefillDate(day);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Add Task on " + day);
                    stage.showAndWait();
                    updateCalendar();
                    dialogStage.close();
                    showWeeklyExpandedView(selectedDate);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            dayColumn.getChildren().add(addButton);
            weekRow.getChildren().add(dayColumn);
        }

        ScrollPane scroll = new ScrollPane(weekRow);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        wrapper.getChildren().add(scroll);

        Scene scene = new Scene(wrapper);
        dialogStage.setScene(scene);
        dialogStage.show();
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
