package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the teacher main page
 * This page is the main page for the teacher, where they can access the question bank, exam management system, and grade statistics
 * The teacher can also exit the system from this page
 */
public class TeacherMainController implements Initializable {

    private String teacher_name;

    public void setTeacherName(String teachername) {
        this.teacher_name = teachername;
    }

    @FXML
    private VBox mainbox;

    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Opens the question bank page
     */
    @FXML
    public void openQuestionManageUI() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("QuestionBank.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == QuestionBankController.class) {
                    QuestionBankController controller = new QuestionBankController();
                    controller.setTeacherId(teacher_name);
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
            stage.setTitle("Question Bank");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Opens the exam management page
     */
    @FXML
    public void openExamManageUI() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ExamManage.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == ExamManageController.class) {
                    ExamManageController controller = new ExamManageController();
                    controller.setTeacherId(teacher_name);
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
            stage.setTitle("Exam Management System");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Opens the grade statistics page
     */
    @FXML
    public void openGradeStatistic() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherGradeStatistic.fxml"));
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == TeacherGradeStatisticController.class) {
                    TeacherGradeStatisticController controller = new TeacherGradeStatisticController();
                    controller.setTeacherId(teacher_name);
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
            stage.setTitle("Grade Statistics");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the system
     */
    @FXML
    public void exit() {
        System.exit(0);
    }

}
