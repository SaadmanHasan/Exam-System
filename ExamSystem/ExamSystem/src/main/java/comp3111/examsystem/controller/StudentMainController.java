package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for handling student main UI functionality.
 * This class is responsible for displaying available quizzes and navigating to the exam UI upon quiz selection.
 */
public class StudentMainController implements Initializable {

    @FXML
    ComboBox<String> examCombox;

    //
    //Custom data members
    //

    ObservableList<String> course_list = FXCollections.observableArrayList();

    private String StudentName;

    //
    //helper function for initialize
    //

    /**
     * Formats the quiz information for display.
     *
     * @param quiz the quiz to format
     * @return the formatted quiz information
     */    private String formatting(Quiz quiz){
        return quiz.getUniqueCourseID_and_examName();
    }

    /**
     * Retrieves a list of quizzes that the student has not taken yet and are published
     *
     * @return a list of quizzes not taken by the student and are published
     */
    private List<Quiz> getAllQuizzesNotTakenByStudent() {
        Database<Quiz> quizDatabase = new Database<>(Quiz.class);
        List<Quiz> all_quiz = quizDatabase.getAll();
        List<Quiz> publishedQuizzes = new ArrayList<>();
        List<Quiz> relevant_quiz = new ArrayList<>();

        for (Quiz q: all_quiz) {
            if (q.getPublished().equals("Yes")) {
                publishedQuizzes.add(q);
            }
        }

        for (Quiz q: publishedQuizzes) {
            boolean toAdd = true;
            for (String studentID: q.getStudents()) {
                if (Objects.equals(studentID, String.valueOf(findingStudentLongID()))) {
                    toAdd = false;
                }
            }
            if (toAdd) {
                relevant_quiz.add(q);
            }
        }

        return relevant_quiz;
    }

    /**
     * Retrieves the student ID for the current student.
     *
     * @return the student ID
     */
    private Long findingStudentLongID() {
        Database<Student> studentDatabase = new Database<>(Student.class);
        List<Student> all_students = studentDatabase.getAll();

        for (Student student: all_students) {
            if (Objects.equals(student.getUsername(), StudentName)) {return student.getId(); }
        }

        //in case you can't find the student
        return null;
    }

    /**
     * Retrieves the selected quiz from the combo box.
     *
     * @return the selected quiz
     */
    private Quiz getSelectedQuiz() {
        String selected = examCombox.getValue();
        List<Quiz> all_quiz = getAllQuizzesNotTakenByStudent();

        for (Quiz quiz: all_quiz) {
            if (Objects.equals(quiz.getUniqueCourseID_and_examName(), selected)) {
                return quiz;
            }
        }
        return null;
    }

    /**
     * Initializes the controller and populates the combo box with available quizzes.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL location, ResourceBundle resources) {

        List<Quiz> all_quiz = getAllQuizzesNotTakenByStudent();

        for (Quiz q: all_quiz) {
            this.course_list.add(formatting(q));
            examCombox.setItems(course_list);
        }

        if (course_list.isEmpty()) {
            examCombox.setPromptText("No available exams");
        }
    }

    //
    //FXML functions
    //

    /**
     * Opens the exam UI for the selected quiz.
     *
     * @param e the action event
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    public void openExamUI(ActionEvent e) throws IOException {

        if (examCombox.getValue() == null) {
            MsgSender.showMsg("Please select an exam");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentTakeExam.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == StudentTakeExamController.class) {
                StudentTakeExamController controller = new StudentTakeExamController();
                controller.setStudentName(this.getStudentName());
                controller.setQuizName(examCombox.getValue());
                controller.setThisQuiz(this.getSelectedQuiz());
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
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        stage.show();
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    /**
     * Opens the grade statistics UI.
     *
     * @param e the action event
     */
    @FXML
    public void openGradeStatistic(ActionEvent e) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentGradeStatistics.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == StudentGradeStatisticController.class) {
                StudentGradeStatisticController controller = new StudentGradeStatisticController();
                controller.setStudentName(this.getStudentName());
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
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        stage.show();
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    /**
     * Exits the application.
     */
    @FXML
    public void exit() {
        System.exit(0);
    }

    /**
     * Gets the student name.
     *
     * @return the student name
     */
    public String getStudentName() {
        return StudentName;
    }


    /**
     * Sets the student name.
     *
     * @param studentName the student name
     */
    public void setStudentName(String studentName) {
        StudentName = studentName;
    }
}
