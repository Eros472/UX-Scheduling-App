/*
Name: Erick Hambardzumyan
Class: CS 2450
Assignment: UX Project: Daily Planner
Date: 05/07/2025
 */

package com.example.javafx_final;

// EditTaskController.java

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditTaskController {
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dueDatePicker;

    @FXML private RadioButton highPriority;
    @FXML private RadioButton mediumPriority;
    @FXML private RadioButton lowPriority;

    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private ComboBox<String> amPmComboBox;

    private ToggleGroup priorityGroup;
    private Task currentTask;

    @FXML
    public void initialize() {
        // Setup toggle group for priority radio buttons
        priorityGroup = new ToggleGroup();
        highPriority.setToggleGroup(priorityGroup);
        mediumPriority.setToggleGroup(priorityGroup);
        lowPriority.setToggleGroup(priorityGroup);

        // Create custom hour spinner with wraparound support (1-12 AM/PM)
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory<Integer>() {
            {
                setValue(12); // Default to 12
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
        hourFactory.setWrapAround(true);

        // Minute spinner 0–59
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteSpinner.setValueFactory(minuteFactory);

        // AM/PM dropdown
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue("AM");

        // Format both spinners visually and input-wise
        formatSpinner(hourSpinner, 2);
        formatSpinner(minuteSpinner, 2);
    }

    // Load task data into form for editing
    public void setTask(Task task) {
        this.currentTask = task;

        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());

        // Extract and parse the due date and time
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            String[] parts = task.getDueDate().split(" ");
            dueDatePicker.setValue(LocalDate.parse(parts[0]));

            if (parts.length > 2) {
                String[] hm = parts[1].split(":");
                int hour = Integer.parseInt(hm[0]);
                int minute = Integer.parseInt(hm[1]);
                String ampm = parts[2];

                hourSpinner.getValueFactory().setValue(hour);
                minuteSpinner.getValueFactory().setValue(minute);
                amPmComboBox.setValue(ampm);
            }
        }

        // Set correct priority radio
        switch (task.getPriority()) {
            case "High": highPriority.setSelected(true); break;
            case "Medium": mediumPriority.setSelected(true); break;
            case "Low": lowPriority.setSelected(true); break;
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        // Apply user input to current task
        currentTask.setTitle(titleField.getText());
        currentTask.setDescription(descriptionField.getText());
        currentTask.setPriority(((RadioButton) priorityGroup.getSelectedToggle()).getText());

        // Build and format 24-hour time
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        String amPm = amPmComboBox.getValue();

        if (amPm.equals("PM") && hour != 12) hour += 12;
        if (amPm.equals("AM") && hour == 12) hour = 0;

        LocalTime time = LocalTime.of(hour, minute);
        String formattedTime = time.format(DateTimeFormatter.ofPattern("hh:mm a"));

        // Combine date and time
        String date = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
        currentTask.setDueDate(date + " " + formattedTime);

        TaskController.updateTaskList();
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }

    // Format spinner input (pad numbers, restrict input to digits)
    private void formatSpinner(Spinner<Integer> spinner, int minDigits) {
        spinner.setEditable(true);
        String format = "%0" + minDigits + "d";

        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            spinner.getEditor().setText(String.format(format, newVal));
        });

        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(oldValue);
                return;
            }

            try {
                int value = Integer.parseInt(newValue);
                int min = ((SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory()).getMin();
                int max = ((SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory()).getMax();
                if (value >= min && value <= max) {
                    spinner.getValueFactory().setValue(value);
                    spinner.getEditor().setText(String.format(format, value));
                } else {
                    spinner.getEditor().setText(oldValue);
                }
            } catch (NumberFormatException e) {
                spinner.getEditor().setText(oldValue);
            }
        });

        spinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                int value = spinner.getValue();
                spinner.getEditor().setText(String.format(format, value));
            }
        });
    }
}
