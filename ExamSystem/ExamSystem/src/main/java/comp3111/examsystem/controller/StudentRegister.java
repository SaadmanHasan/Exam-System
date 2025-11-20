//Aarav
package comp3111.examsystem.controller;
import comp3111.examsystem.Classes.Student;

import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Controller class for handling student registration functionality.
 * This class is responsible for capturing student details, validating the input,
 * and adding the student to the database upon successful registration.
 */
public class StudentRegister {

    ObservableList<String> genderlist = FXCollections.observableArrayList("Male", "Female");

    //Capturing all field's information
    @FXML
    ComboBox<String> gender;
    @FXML
    TextField username;
    @FXML
    TextField name;
    @FXML
    TextField age;
    @FXML
    PasswordField password;
    @FXML
    PasswordField confirm_password;
    @FXML
    TextField department;

    @FXML
    Button exitButton;

    //
    //helper functions for the validate()
    //

    /**
     * Checks if all fields are empty
     *
     * @return true if all fields are not empty
     */
    public Boolean checkIfAllEmpty() {
        return !username.getText().isEmpty() &&
                !password.getText().isEmpty() &&
                !name.getText().isEmpty() &&
                !age.getText().isEmpty() &&
                !department.getText().isEmpty() &&
                !gender.getValue().isEmpty() &&
                !confirm_password.getText().isEmpty();
    }

    /**
     * Checks if the password and confirm password fields match
     *
     * @return true if the passwords match
     */
    public Boolean passwords_match() {
        return Objects.equals(confirm_password.getText(), password.getText());
    }

    /**
     * Checks for the right range of the Age entered by the user
     *
     * @return true if age is an integer and within the range of 6 to 100 inclusive
     */
    public Boolean validate_age() {
        return age.getText().matches("\\d+") &&
                Integer.parseInt(age.getText()) >= 6 && Integer.parseInt(age.getText()) <= 100;
    }

    /**
     * Checks if the username is unique
     *
     * @return true if the username is unique
     */
    public Boolean validate_username() {
        Database<Student> manageStudents = new Database<>(Student.class);
        List<Student> students = manageStudents.getAll();
        for (Student student : students) {
            if (student.getUsername().equals(username.getText())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates all fields
     *
     * @return true if all fields are valid
     */
    public Boolean validate() {

        //Check if Empty
        if (!checkIfAllEmpty()) {
            MsgSender.showMsg("Please write all fields!");
            return false;
        }
        //Check age if integer only and in the right range of 6 to 100
        if (!validate_age()) {
            MsgSender.showMsg("Please write a positive Integer value within the range of 6 and 100 inclusive");
            age.clear();
            return false;
        }
        //Check if password and confirm password match
        if (!passwords_match()) {
            MsgSender.showMsg("Did a mistake? Passwords don't match");
            password.clear();
            confirm_password.clear();
            return false;
        }
        //Checks if the username is unique
        if (!validate_username()) {
            MsgSender.showMsg("Username already exists. Please choose another one.");
            username.clear();
            return false;
        }

        return true;
    }

    //
    //FXML functions
    //

    /**
     * Initializes
     */
    @FXML
    private void initialize(){
        Platform.runLater(() -> {
            gender.setValue("Male");
            gender.setItems(genderlist);
        });
    }

    /**
     * Exits the program
     *
     * @param e ActionEvent
     */
    @FXML
    public void exit(ActionEvent e){
        Platform.runLater(() -> {
            if (exitButton.getScene() != null && exitButton.getScene().getWindow() != null) {
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * Checks all validation tests and if they pass, adds the student to the database
     *
     * @param e ActionEvent
     */
    @FXML
    public void register(ActionEvent e){

        Platform.runLater(() -> {
            Database<Student> manageStudents = new Database<>(Student.class);

            //Runs validation tests as per the above function
            if (!validate()) {return;}

            //Capture the above values here
            String input_gender = gender.getValue();
            String input_username = username.getText();
            String input_name = name.getText();
            String input_age = age.getText();
            String input_password = password.getText();
            String input_department = department.getText();
            Long assigned_id = System.currentTimeMillis();

            //Need to check for extreme values
            //Need to check for right formatting

            //For now, assume no validation errors
            Student addThisStudent = new Student(assigned_id, input_username,
                    input_password, input_name, input_age, input_department, input_gender);

            //Adding this student to the DB
            manageStudents.add(addThisStudent);

            //          Remarks
            //          Check if there is a condition where the student is not being added to the DB
            //          considering the prev test cases

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentMainUI.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == StudentMainController.class) {
                    StudentMainController controller = new StudentMainController();
                    controller.setStudentName(input_username);
                    return controller;
                } else {
                    try {
                        return controllerClass.getDeclaredConstructor().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Hi " + input_username + ", Welcome to HKUST Examination System");
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            stage.show();
            if (e.getSource() instanceof Button) {
                Button sourceButton = (Button) e.getSource();
                if (sourceButton.getScene() != null && sourceButton.getScene().getWindow() != null) {
                    ((Stage) sourceButton.getScene().getWindow()).close();
                }
            }
        });
    }
}
