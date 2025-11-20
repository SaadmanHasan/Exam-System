package comp3111.examsystem.controller;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;

//was giving issues with the below three. If not added, Aarav cant update
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;

/**
 * The controller for the teacher login page
 */
public class TeacherLoginController {
    @FXML
    TextField usernameTxt;
    @FXML
    PasswordField passwordTxt;


    /**
     * For Validation
     * @param username
     * @param password
     * @return
     */
    boolean CheckIfExists(String username, String password) {
        Database<Teacher> teacherdatabase = new Database<>(Teacher.class);

        List<Teacher> all_teachers = teacherdatabase.getAll();

        //checking among the chosen teachers (with the given username), who has the given password
        for (Teacher t: all_teachers) {
            if (Objects.equals(t.getPassword(), password) && Objects.equals(t.getUsername(), username)) return true;
        }
        return false;
    }

    /**
     * login function for teachers. It checks if the username and password are correct
     * If the username and password are correct, it will open the teacher main page
     * If the username and password are incorrect, it will show an error message
     */
    @FXML
    public void login(ActionEvent e) {
        Platform.runLater(() -> {
            String input_username = usernameTxt.getText();
            String input_password = passwordTxt.getText();

            if (input_username == null || input_username.isEmpty()) {
                showAlert("Please enter your username");
                return;
            }
            if (input_password == null || input_password.isEmpty()) {
                showAlert("Please enter your password");
                return;
            }

            boolean res =  CheckIfExists(usernameTxt.getText(), passwordTxt.getText());

            if(!res){
                MsgSender.showMsg("Invalid Username or Password");
                usernameTxt.clear();
                passwordTxt.clear();
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherMainUI.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == TeacherMainController.class) {
                    TeacherMainController controller = new TeacherMainController();
                    controller.setTeacherName(usernameTxt.getText());
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
     * Function to open the register page for teachers
     */
    @FXML
    public void register(ActionEvent e) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherRegister.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Welcome to the Register Page");
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            stage.show();
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
