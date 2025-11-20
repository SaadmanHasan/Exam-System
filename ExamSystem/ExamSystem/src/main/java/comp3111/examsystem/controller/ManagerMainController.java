package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for managing the main UI for managers in the exam system.
 */
public class ManagerMainController implements Initializable {
    @FXML
    private VBox mainbox;
    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources the resources used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {
    }
    /**
     * Opens the Student Manager UI. This method is triggered when the corresponding button is clicked.
     */
    @FXML
    public void openStudentManageUI() {
       try{
           FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentManager.fxml"));
           Stage stage = new Stage();
           stage.setTitle("Student Manager");
           stage.setScene(new Scene(fxmlLoader.load()));
           stage.show();

       } catch (Exception e){
        e.printStackTrace();
    }
    }
    /**
     * Opens the Teacher Manager UI. This method is triggered when the corresponding button is clicked.
     */
    @FXML
    public void openTeacherManageUI() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherManager.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Teacher Manager");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Opens the Course Manager UI. This method is triggered when the corresponding button is clicked.
     */
    @FXML
    public void openCourseManageUI() {
        try{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("CourseManager.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Course Manager");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();

    } catch (Exception e){
        e.printStackTrace();
    }
    }
    /**
     * Exits the application. This method is triggered when the exit button is clicked.
     */
    @FXML
    public void exit() {
        System.exit(0);
    }
}
