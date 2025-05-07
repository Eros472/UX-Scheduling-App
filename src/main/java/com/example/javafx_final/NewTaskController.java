package com.example.javafx_final;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NewTaskController {
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

    @FXML
    public void initialize() {
        // Priority toggle setup
        priorityGroup = new ToggleGroup();
        highPriority.setToggleGroup(priorityGroup);
        mediumPriority.setToggleGroup(priorityGroup);
        lowPriority.setToggleGroup(priorityGroup);
        highPriority.setSelected(true);

        // Hour spinner (1–12) with manual input and digit validation
        SpinnerValueFactory<Integer> hourFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 12);
        hourSpinner.setValueFactory(hourFactory);
        formatSpinner(hourSpinner, 2);

        // Minute spinner (0–59) with manual input and digit validation
        SpinnerValueFactory<Integer> minuteFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteSpinner.setValueFactory(minuteFactory);
        formatSpinner(minuteSpinner, 2);

        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue("AM");
    }

    public void prefillDate(LocalDate date) {
        if (dueDatePicker != null) {
            dueDatePicker.setValue(date);
        }
    }


    private void formatSpinner(Spinner<Integer> spinner, int minDigits) {
        spinner.setEditable(true);

        // Formatter for padding
        String formatString = "%0" + minDigits + "d";

        // Force formatting when the value changes via arrows
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                spinner.getEditor().setText(String.format(formatString, newVal));
            }
        });

        // Prevent invalid input
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
                    spinner.getEditor().setText(String.format(formatString, value));
                } else {
                    spinner.getEditor().setText(oldValue);
                }
            } catch (NumberFormatException e) {
                spinner.getEditor().setText(oldValue);
            }
        });

        // Apply formatting when field loses focus
        spinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                int value = spinner.getValue();
                spinner.getEditor().setText(String.format(formatString, value));
            }
        });
    }




    @FXML
    public void handleSave(ActionEvent event) {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String dueDate = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
        String priority = ((RadioButton) priorityGroup.getSelectedToggle()).getText();

        // Time conversion to 24-hour format
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        String amPm = amPmComboBox.getValue();

        if (amPm.equals("PM") && hour != 12) hour += 12;
        if (amPm.equals("AM") && hour == 12) hour = 0;

        LocalTime time = LocalTime.of(hour, minute);
        String formattedTime = time.format(DateTimeFormatter.ofPattern("hh:mm a"));

        // Append time to dueDate string or store separately
        String fullDueDate = dueDate + " " + formattedTime;

        Task newTask = new Task(title, description, fullDueDate, priority);
        TaskController.tasks.add(newTask);
        TaskController.updateTaskList();
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }
}
