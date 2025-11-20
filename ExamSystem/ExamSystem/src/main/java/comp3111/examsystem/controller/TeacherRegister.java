package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * The controller for the teacher registration page
 */
public class TeacherRegister {

    ObservableList<String> genderlist = FXCollections.observableArrayList("Male", "Female");
    ObservableList<String> positionlist = FXCollections.observableArrayList("Chair", "Adjunct", "Assistant", "Lecturer");

    @FXML
    private ComboBox<String> gender;
    @FXML
    private ComboBox<String> position;
    @FXML
    TextField username;
    @FXML
    TextField name;
    @FXML
    TextField age;
    @FXML
    TextField department;
    @FXML
    PasswordField password;
    @FXML
    PasswordField confirm_password;

    Database<Teacher> teacherDB = new Database<>(Teacher.class);

    /**
     * Initializes the register form
     */
    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            gender.setValue("Male");
            gender.setItems(genderlist);

            position.setValue("Chair");
            position.setItems(positionlist);
        });
    }

    /**
     * Exits the registration page
     */
    @FXML
    public void exit(ActionEvent e) {
        Platform.runLater(() -> {
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        });
    }

    /**
     * Registers the teacher
     * Checks if all fields are filled out
     * Checks if the username already exists
     * Checks if the age is an integer
     * Checks if the password and confirm password match
     * If all checks pass, registers the teacher
     * Shows a message dialog if the teacher is successfully registered
     * Closes the registration page
     */
    @FXML
    public void register(ActionEvent e) {
        Platform.runLater(() -> {
            Database<Teacher> manageTeachers = new Database<>(Teacher.class);
            String input_username = username.getText();
            String input_password = password.getText();
            String input_name = name.getText();
            String input_age = age.getText();
            String input_department = department.getText();
            String input_password_confirm = confirm_password.getText();
            String input_gender = gender.getValue();
            String input_position = position.getValue();

            if (input_username == null || input_username.isEmpty() ||
                    input_password == null || input_password.isEmpty() ||
                    input_name == null || input_name.isEmpty() ||
                    input_age == null || input_age.isEmpty() ||
                    input_department == null || input_department.isEmpty() ||
                    input_password_confirm == null || input_password_confirm.isEmpty() ||
                    input_gender == null || input_gender.isEmpty() ||
                    input_position == null || input_position.isEmpty()) {
                MsgSender.showMsg("Please Write information to all Fields");
                return;
            }

            List<Teacher> teachers = manageTeachers.getAll();
            for (Teacher teacher : teachers) {
                if (teacher.getUsername().equals(input_username)) {
                    MsgSender.showMsg("Username already exists");
                    return;
                }
            }
            try {
                int int_age = Integer.parseInt(input_age);
                if (int_age < 0) {
                    MsgSender.showMsg("Age cannot be negative");
                    return;
                }
            } catch (Exception exception) {
                MsgSender.showMsg("Wrong Formatting for Age. Please input an Integer");
                return;
            }
            try {
                int int_age = Integer.parseInt(input_age);
                if (int_age < 15 || int_age > 100) {
                    MsgSender.showMsg("Please Enter a Valid Age");
                    return;
                }
            } catch (Exception exception) {
                MsgSender.showMsg("Wrong Formatting for Age. Please input an Integer");
                return;
            }

            if (!input_password.equals(input_password_confirm)) {
                MsgSender.showMsg("Please Confirm your password");
                return;
            }

            Long uniqueId = System.currentTimeMillis();
            manageTeachers.add(new Teacher(uniqueId, input_username, input_password, input_name, input_age, input_department,
                    input_gender, input_position));

            MsgSender.showMsg("Successfully Registered");
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        });
    }
}