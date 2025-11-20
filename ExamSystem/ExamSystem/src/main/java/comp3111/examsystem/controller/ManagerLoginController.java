package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.Classes.Manager;
import comp3111.examsystem.Utils.Database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static comp3111.examsystem.Utils.MsgSender.showConfirm;
import static comp3111.examsystem.Utils.MsgSender.showMsg;


/**
 * Controller class for managing the login functionality for managers in the exam system.
 */
public class ManagerLoginController implements Initializable {
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources the resources used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {
    }
    /**
     * Handles the login action. This method is triggered when the login button is clicked.
     *
     * @param e the action event triggered by the login button.
     */
    @FXML
    public void login(ActionEvent e) {
        Database<Manager> managerUserDatabase = new Database<>(Manager.class);
        List<Manager> managerList = managerUserDatabase.getAll();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ManagerMainUI.fxml"));
        Stage stage = new Stage();
        String input_username = usernameTxt.getText();
        String input_password = passwordTxt.getText();
        if (input_username == null || input_username.isEmpty()) {
            showMsg("Please enter your username");
            return;
        }
        if (input_password == null || input_password.isEmpty()) {
            showMsg("Please enter your password");
            return;
        }
        boolean isLogin = false;
        for (Manager o : managerList) {
            if (o.checkEntry(input_username, input_password)) {
                isLogin = true;
                break;
            }
        }
        if (!isLogin) {
            showMsg("Invalid username or password");
            return;
        }else{
            showConfirm("Hint", "Login successful", new Runnable() {
                @Override
                public void run() {
                    stage.setTitle("Hi " + usernameTxt.getText() + ", Welcome to HKUST Examination System");
                    try {
                        stage.setScene(new Scene(fxmlLoader.load()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    stage.show();
                    ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
                }
            });
        }

    }

}
