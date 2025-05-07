package com.example.javafx_final;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class NewTaskController {
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dueDatePicker;

    @FXML private RadioButton highPriority;
    @FXML private RadioButton mediumPriority;
    @FXML private RadioButton lowPriority;

    private ToggleGroup priorityGroup;

    @FXML
    public void initialize() {
        priorityGroup = new ToggleGroup();
        highPriority.setToggleGroup(priorityGroup);
        mediumPriority.setToggleGroup(priorityGroup);
        lowPriority.setToggleGroup(priorityGroup);
        highPriority.setSelected(true); // Default
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String dueDate = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
        String priority = ((RadioButton) priorityGroup.getSelectedToggle()).getText();

        Task newTask = new Task(title, description, dueDate, priority);
        TaskController.tasks.add(newTask);
        TaskController.updateTaskList();
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }
}
