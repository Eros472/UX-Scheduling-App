/*
Name: Erick Hambardzumyan
Class: CS 2450
Assignment: UX Project: Daily Planner
Date: 05/07/2025
 */

package com.example.javafx_final;

// NewTaskController.java

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NewTaskController {
    // FXML input fields for task details
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dueDatePicker;

    // Priority selection radio buttons
    @FXML private RadioButton highPriority;
    @FXML private RadioButton mediumPriority;
    @FXML private RadioButton lowPriority;

    // Time input spinners and AM/PM selector
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<String> amPmComboBox;

    private ToggleGroup priorityGroup; // Group for priority radio buttons

    @FXML
    public void initialize() {
        // Initialize priority toggle group and select default
        priorityGroup = new ToggleGroup();
        highPriority.setToggleGroup(priorityGroup);
        mediumPriority.setToggleGroup(priorityGroup);
        lowPriority.setToggleGroup(priorityGroup);
        highPriority.setSelected(true);

        // Custom 12-hour spinner that wraps around from 12 to 1
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory<Integer>() {
            {
                setValue(12);
            }

            @Override
            public void decrement(int steps) {
                int current = getValue();
                int newVal = current - steps;
                if (newVal < 1) newVal = 12 - ((1 - newVal) % 12);
                setValue(newVal);
            }

            @Override
            public void increment(int steps) {
                int current = getValue();
                int newVal = current + steps;
                if (newVal > 12) newVal = ((newVal - 1) % 12) + 1;
                setValue(newVal);
            }
        };
        hourSpinner.setValueFactory(hourFactory);

        // Minute spinner (0–59)
        SpinnerValueFactory<Integer> minuteFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteSpinner.setValueFactory(minuteFactory);
        formatSpinner(minuteSpinner, 2); // Format with leading zeros

        // Set default AM/PM value
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue("AM");
    }

    // Prefills the selected date when opening the task editor
    public void prefillDate(LocalDate date) {
        if (dueDatePicker != null) {
            dueDatePicker.setValue(date);
        }
    }

    // Format spinner text and restrict input to valid numeric values
    private void formatSpinner(Spinner<Integer> spinner, int minDigits) {
        spinner.setEditable(true);

        SpinnerValueFactory<Integer> factory = spinner.getValueFactory();
        if (factory instanceof SpinnerValueFactory.IntegerSpinnerValueFactory intFactory) {
            intFactory.setWrapAround(true); // Enables wrap-around scroll
        }

        String formatString = "%0" + minDigits + "d";

        // Format value visually when changed via arrows
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                spinner.getEditor().setText(String.format(formatString, newVal));
            }
        });

        // Limit manual input to digits only and clamp to valid range
        spinner.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                spinner.getEditor().setText(oldText);
                return;
            }

            try {
                int value = Integer.parseInt(newText);
                int min = ((SpinnerValueFactory.IntegerSpinnerValueFactory) factory).getMin();
                int max = ((SpinnerValueFactory.IntegerSpinnerValueFactory) factory).getMax();
                if (value >= min && value <= max) {
                    factory.setValue(value);
                } else if (value < min) {
                    factory.setValue(max);
                } else {
                    factory.setValue(min);
                }
            } catch (NumberFormatException e) {
                spinner.getEditor().setText(oldText);
            }
        });

        // Pad with zeros when spinner loses focus
        spinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                spinner.getEditor().setText(String.format(formatString, spinner.getValue()));
            }
        });
    }

    // Save button: creates new Task and closes window
    @FXML
    public void handleSave(ActionEvent event) {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String dueDate = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
        String priority = ((RadioButton) priorityGroup.getSelectedToggle()).getText();

        // Convert user-entered hour/minute to 24-hour format
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        String amPm = amPmComboBox.getValue();

        if (amPm.equals("PM") && hour != 12) hour += 12;
        if (amPm.equals("AM") && hour == 12) hour = 0;

        LocalTime time = LocalTime.of(hour, minute);
        String formattedTime = time.format(DateTimeFormatter.ofPattern("hh:mm a"));

        // Append formatted time to date string
        String fullDueDate = dueDate + " " + formattedTime;

        // Add the new task to the shared task list
        Task newTask = new Task(title, description, fullDueDate, priority);
        TaskController.tasks.add(newTask);
        TaskController.updateTaskList();

        // Close the task window
        ((Stage) titleField.getScene().getWindow()).close();
    }

    // Cancel button: closes the window without saving
    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }
}
