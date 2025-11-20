//Aarav

package comp3111.examsystem.controller;

import comp3111.examsystem.Utils.FileUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

//Below is not part of the code template
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import comp3111.examsystem.Classes.Student;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Controller class for handling student login functionality.
 * This class is responsible for verifying student credentials and navigating to the main UI upon successful login.
 */
public class StudentLoginController implements Initializable {

    @FXML
    TextField usernameTxt;
    @FXML
    PasswordField passwordTxt;

    public Boolean loggedIn = false;

    /**
     * Initializes the controller.
     */
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * @return true if all fields are empty
     */
    public Boolean checkIfAllEmpty() {
        return (usernameTxt.getText().isEmpty() &&
                passwordTxt.getText().isEmpty());
    }

    /**
     * Check if the student with the given username and password exists in the database
     * @param password the password of the student
     * @param username the username of the student
     * @return true if the student with the given username and password exists in the database
     */
    public boolean CheckIfExists(String username, String password) {
        // Focus is to check whether the username and password exists in the DB or not. Return true if it does, else return false
        Database<Student> manageStudentDatabase = new Database<>(Student.class);

        //get all student ids with this username and password
        // Assumption 1 --> there should be only one

        //getting all the students with this username
        List<Student> all_students = manageStudentDatabase.getAll();

        //checking among the chosen students (with the given username), who has the given password
        for (Student s: all_students) {
            if (Objects.equals(s.getPassword(), password) && Objects.equals(s.getUsername(), username)) return true;
        }
        return false;
    }

    /**
     * @return true if the student's credentials are valid (exists in the DB and no fields are empty)
     */
    public Boolean validate() {
        if (checkIfAllEmpty() || (!CheckIfExists(usernameTxt.getText(), passwordTxt.getText()))) {
            Platform.runLater(() -> {
                MsgSender.showMsg("We couldn't find you :( Please Double check your credentials");
                usernameTxt.clear();
                passwordTxt.clear();
            });
            return false;
        }
        return true;
    }

    /**
     * Logs in the student if the credentials are valid
     *
     * @param e the event that triggers the login
     * @throws IOException when the next screen is not found
     */
    @FXML
    public void login(ActionEvent e) throws IOException {
        Platform.runLater(() -> {
            //  --> Verifying the student's credentials
            if (!validate()) {
                return;
            }

            loggedIn = true;
            MsgSender.showMsg("Login Successful");

            //Load the next screen
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentMainUI.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == StudentMainController.class) {
                    StudentMainController controller = new StudentMainController();
                    controller.setStudentName(usernameTxt.getText());
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
            stage.setTitle("Hi " + usernameTxt.getText() + ", Welcome to HKUST Examination System");
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            stage.show();
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        });
    }

    /**
     * Loads the Register screen when the Register button is clicked
     *
     * @param e the event that triggers the registration
     */
    @FXML
    public void register(ActionEvent e) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentRegister.fxml"));
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            stage.show();
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        });
    }
}
