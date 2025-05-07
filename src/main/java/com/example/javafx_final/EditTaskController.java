package com.example.javafx_final;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditTaskController {
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dueDatePicker;

    @FXML private RadioButton highPriority;
    @FXML private RadioButton mediumPriority;
    @FXML private RadioButton lowPriority;

    private ToggleGroup priorityGroup;

    private Task currentTask;

    @FXML
    public void initialize() {
        priorityGroup = new ToggleGroup();
        highPriority.setToggleGroup(priorityGroup);
        mediumPriority.setToggleGroup(priorityGroup);
        lowPriority.setToggleGroup(priorityGroup);
    }

    public void setTask(Task task) {
        this.currentTask = task;
        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());
        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            dueDatePicker.setValue(java.time.LocalDate.parse(task.getDueDate()));
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
        currentTask.setDueDate(dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "");
        currentTask.setPriority(((RadioButton) priorityGroup.getSelectedToggle()).getText());
        TaskController.updateTaskList();
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage) titleField.getScene().getWindow()).close();
    }
}
