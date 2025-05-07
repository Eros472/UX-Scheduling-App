package com.example.javafx_final;

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
        // Priority radio setup
        priorityGroup = new ToggleGroup();
        highPriority.setToggleGroup(priorityGroup);
        mediumPriority.setToggleGroup(priorityGroup);
        lowPriority.setToggleGroup(priorityGroup);

        // Time Picker setup
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 12);
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);

        hourSpinner.setValueFactory(hourFactory);
        minuteSpinner.setValueFactory(minuteFactory);
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue("AM");

        formatSpinner(hourSpinner, 2);
        formatSpinner(minuteSpinner, 2);
    }

    public void setTask(Task task) {
        this.currentTask = task;

        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());

        // Parse date and time from combined string
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

        switch (task.getPriority()) {
            case "High": highPriority.setSelected(true); break;
            case "Medium": mediumPriority.setSelected(true); break;
            case "Low": lowPriority.setSelected(true); break;
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        currentTask.setTitle(titleField.getText());
        currentTask.setDescription(descriptionField.getText());
        currentTask.setPriority(((RadioButton) priorityGroup.getSelectedToggle()).getText());

        // Format time from pickers
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        String amPm = amPmComboBox.getValue();

        if (amPm.equals("PM") && hour != 12) hour += 12;
        if (amPm.equals("AM") && hour == 12) hour = 0;

        LocalTime time = LocalTime.of(hour, minute);
        String formattedTime = time.format(DateTimeFormatter.ofPattern("hh:mm a"));

        String date = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
        currentTask.setDueDate(date + " " + formattedTime);

        TaskController.updateTaskList();
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }

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
