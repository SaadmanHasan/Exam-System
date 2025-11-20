package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Course;
import comp3111.examsystem.Classes.Question;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for managing the student grade statistics view.
 * This class is responsible for initializing the view, populating the table and bar chart with quiz data,
 * and handling user interactions such as filtering and resetting the view.
 */
public class StudentGradeStatisticController implements Initializable {

    @FXML
    private BarChart<?, ?> barChart;

    @FXML
    private CategoryAxis categoryAxisBar;

    @FXML
    private TableColumn<Quiz, String> courseCol;

    @FXML
    private TableColumn<Quiz, String> examCol;

    @FXML
    private Button filterButton;

    @FXML
    private TableView<Quiz> filterTable;

    @FXML
    private NumberAxis numberAxisBar;

    @FXML
    private Button resetButton;

    @FXML
    private ComboBox<String> showCourses;

    @FXML
    private TableColumn<Quiz, String> timeTakenCol;

    @FXML
    private TableColumn<Quiz, String> totalScoreCol;

    @FXML
    private TableColumn<Quiz, String> yourScoreCol;

    @FXML
    private TableColumn<Quiz, String> meanCol;

    //
    // Custom data members
    //

    private String studentName;

    private List<Quiz> quizzesTakenByStudent = new ArrayList<>();

    private List<Course> coursesTakenByStudent = new ArrayList<>();

    private ObservableList<Quiz> observableQuizzes;

    //
    //Helper Functions
    //

    /**
     * Retrieves the ID of the student with the username stored in the `studentName` field.
     *
     * @return the ID of the student if found, otherwise null
     */
    private Long findingStudentLongID() {
        Database<Student> studentDatabase = new Database<>(Student.class);
        List<Student> all_students = studentDatabase.getAll();

        for (Student student: all_students) {
            if (Objects.equals(student.getUsername(), studentName)) {return student.getId(); }
        }

        //in case you can't find the student
        return null;
    }

    /**
     * Populates the list of quizzes taken by the student.
     */
    private void setQuizzesTakenByStudent() {
        Database<Quiz> quizDatabase = new Database<>(Quiz.class);
        List<Quiz> all_quiz = quizDatabase.getAll();

        for (Quiz q: all_quiz) {
            boolean toAdd = false;
            for (String studentID: q.getStudents()) {
                if (Objects.equals(studentID, String.valueOf(findingStudentLongID()))) {
                    toAdd = true;
                }
            }
            if (toAdd) {
                quizzesTakenByStudent.add(q);
            }
        }

        System.out.println("Quizzes taken by student: " + quizzesTakenByStudent);
    }

    /**
     * Finds a course by its ID.
     *
     * @param courseID the ID of the course
     * @return the course if found, otherwise null
     */
    private Course findCourse(String courseID) {
        Database<Course> courseDatabase = new Database<>(Course.class);
        List<Course> all_courses = courseDatabase.getAll();
        for (Course course: all_courses) {
            if (Objects.equals(course.getCourseID(), courseID)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Retrieves the quizzes for a specific course.
     *
     * @param CourseId the ID of the course
     * @return the list of quizzes for the course
     */
    private List<Quiz> getQuizzesObject(String CourseId) {
        List<Quiz> quizzes = new ArrayList<>();
        for (Quiz quiz: quizzesTakenByStudent) {
            if (Objects.equals(quiz.getCourseID(), CourseId)) {
                quizzes.add(quiz);
            }
        }
        return quizzes;
    }

    /**
     * Populates the list of courses taken by the student.
     */
    private void setCoursesTakenByStudent() {
        List<String> courseIDTaken = new ArrayList<>();

        for (Quiz quiz: quizzesTakenByStudent) {
            if (quiz != null && quiz.getStudents().contains(String.valueOf(findingStudentLongID()))) {
                if (!courseIDTaken.contains(quiz.getCourseID())) {
                    courseIDTaken.add(quiz.getCourseID());
                    //find this course in the database and add it to coursesTakenByStudent
                    coursesTakenByStudent.add(findCourse(quiz.getCourseID()));
                }
            } else {
                System.out.println("Quiz is null");
            }
        }

        System.out.println("Courses taken by student: " + coursesTakenByStudent);
    }

    /**
     * Formats the course name for display.
     *
     * @param course the course
     * @return the formatted course name
     */
    private String formattingCourseName(Course course) {

        if (course == null) {
            System.out.println("Course is null");
            return "";
        }

        return course.getCourseID() + " - " + course.getCourseName();
    }

    /**
     * Deformats the course name to retrieve the course ID.
     *
     * @param courseName the formatted course name
     * @return the course ID
     */
    private String deformattingCourseName(String courseName) {
        return courseName.split(" - ")[0];
    }

    /**
     * Populates the course combo box with the courses taken by the student.
     */
    private void populateCourseComboBox() {
        List<String> courses_Name = new ArrayList<>();
        for (Course course: coursesTakenByStudent) {
            courses_Name.add(formattingCourseName(course));
            System.out.println("Course: " + course);
        }
        showCourses.setItems(FXCollections.observableArrayList(courses_Name));
    }


    /**
     * Retrieves the quizzes for a specific course and exam name.
     *
     * @param uniqueCourseID_and_examName the unique course ID and exam name
     * @return the list of quizzes for the course and exam name
     */
    private List<Quiz> getQuizzesByUniqueCourseID_and_examName(String uniqueCourseID_and_examName) {
        List<Quiz> quizzesForThisCourse = new ArrayList<>();
        for (Quiz quiz: quizzesTakenByStudent) {
            if (Objects.equals(quiz.getUniqueCourseID_and_examName(), uniqueCourseID_and_examName)) {
                quizzesForThisCourse.add(quiz);
            }
        }
        return quizzesForThisCourse;
    }


    /**
     * Populates the table with the specified quizzes.
     *
     * @param quizzes the list of quizzes
     */
    private void populateTableByQuizzes(List<Quiz> quizzes) {
        this.observableQuizzes = FXCollections.observableArrayList(quizzes);
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        examCol.setCellValueFactory(new PropertyValueFactory<>("examName"));
        totalScoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        yourScoreCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quiz, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Quiz, String> param) {
                Quiz quiz = param.getValue();
                // Replace this with your function to get the score
                Long score = quiz.getStudentScore(String.valueOf(findingStudentLongID()));
                return new SimpleStringProperty(String.valueOf(score));
            }
        });
        timeTakenCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quiz, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Quiz, String> param) {
                Quiz quiz = param.getValue();
                // Replace this with your function to get the score
                Long score = quiz.getStudentTimeSpent(String.valueOf(findingStudentLongID()));
                return new SimpleStringProperty(String.valueOf(score));
            }
        });
        meanCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quiz, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Quiz, String> param) {
                Quiz quiz = param.getValue();
                Double mean = quiz.getMean();
                if (mean != null) {
                    return new SimpleStringProperty(String.valueOf(mean));
                } else {
                    return new SimpleStringProperty("N/A");
                }
                //return new SimpleStringProperty(String.valueOf(quiz.getMean()));
            }
        });

        filterTable.setItems(observableQuizzes);
    }

    /**
     * Populates the bar chart with the specified quizzes.
     *
     * @param quizzes the list of quizzes
     */
    private void populateBarChart(List<Quiz> quizzes) {
        XYChart.Series series = new XYChart.Series();
        series.setName("Exam");

        if (quizzes.size() == 1) {
            //Make two dummy data to look clean
            series.getData().add(new XYChart.Data("", 0));
            series.getData().add(new XYChart.Data("", 0));

        }

        for (Quiz quiz: quizzes) {
            Long score = quiz.getStudentScore(String.valueOf(findingStudentLongID()));
            series.getData().add(new XYChart.Data(quiz.getCourseID() + "|" + quiz.getExamName(), score));
        }
        barChart.getData().add(series);
    }

    /**
     * Retrieves the quizzes for a specific course.
     *
     * @param course the course
     * @return the list of quizzes for the course
     */
    private List<Quiz> getQuizzesByCourse(Course course) {
        List<Quiz> quizzesForThisCourse = new ArrayList<>();
        for (Quiz quiz: quizzesTakenByStudent) {
            if (Objects.equals(quiz.getCourseID(), course.getCourseID())) {
                quizzesForThisCourse.add(quiz);
            }
        }
        return quizzesForThisCourse;
    }


    /**
     * Initializes the controller.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL location, ResourceBundle resources) {
        setQuizzesTakenByStudent(); // quizzesTakenByStudent is now populated
        setCoursesTakenByStudent();
        populateCourseComboBox(); // Populate the course combobox with the courses taken by the student

        //Populating the bar and table with all courses
        populateTableByQuizzes(quizzesTakenByStudent);
        populateBarChart(quizzesTakenByStudent);
    }

    //
    //FXML Functions
    //

    /**
     * Filters the quizzes based on the selected course.
     *
     * @param event the action event
     */
    @FXML
    void filter(ActionEvent event) {

        //Clearing the table
        filterTable.getItems().clear();
        //clearing the bar chart
        barChart.getData().clear();

        //Getting the quizzes for the selected course
        List<Quiz> quizzes = getQuizzesObject(deformattingCourseName(showCourses.getValue()));

        if (quizzes.isEmpty()) {
            MsgSender.showMsg("You have no Quizzes for this course");
            return;
        }

        populateTableByQuizzes(quizzes);
        populateBarChart(quizzes);
    }

    /**
     * Resets the table and bar chart to the original state.
     *
     * @param event the action event
     */
    @FXML
    void reset(ActionEvent event) {
        //Clear the bar chart
        barChart.getData().clear();

        //Clear the table
        filterTable.getItems().clear();

        //Going back to the original state
        populateTableByQuizzes(quizzesTakenByStudent);
        populateBarChart(quizzesTakenByStudent);
    }

    @FXML
    void showCourses(ActionEvent event) {}

    /**
     * Goes back to the StudentMainUI
     *
     */
    @FXML
    void goBack(ActionEvent e) {
        //Going back to the StudentMainUI
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentMainUI.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == StudentMainController.class) {
                StudentMainController controller = new StudentMainController();
                controller.setStudentName(studentName);
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
        stage.setTitle("Hi " + studentName + ", Welcome to HKUST Examination System");
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        stage.show();
        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    //
    // Helper functions --> getters and setters
    //



    /**
     * Gets the student name.
     *
     * @return the student name
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Sets the student name.
     *
     * @param studentName the student name
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


    /**
     * Gets the observable list of quizzes.
     *
     * @return the observable list of quizzes
     */
    public ObservableList<Quiz> getObservableQuizzes() {
        return observableQuizzes;
    }

    /**
     * Sets the observable list of quizzes.
     *
     * @param observableQuizzes the observable list of quizzes
     */
    public void setObservableQuizzes(ObservableList<Quiz> observableQuizzes) {
        this.observableQuizzes = observableQuizzes;
    }
}
