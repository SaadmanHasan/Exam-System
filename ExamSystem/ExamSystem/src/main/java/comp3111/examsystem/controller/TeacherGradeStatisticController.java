package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Course;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static comp3111.examsystem.Utils.MsgSender.showMsg;
/**
 * Controller class for viewing grade statistics for teachers in the exam system.
 */
public class TeacherGradeStatisticController implements Initializable {
    /**
     * A class representing a grade example for a student in a specific course and exam.
     */
    public static class GradeExampleClass {
        private final String studentName;
        private final String courseNum;
        private final String examName;
        private String score;
        private String fullScore;
        private String timeSpend;
        /**
         * Constructs a GradeExampleClass object with the specified quiz, student, score, and time spent.
         *
         * @param quiz the quiz associated with the grade
         * @param student the student associated with the grade
         * @param score the score obtained by the student
         * @param timeSpend the time spent by the student on the exam
         */
        public GradeExampleClass(Quiz quiz, Student student, String score, String timeSpend) {
            this.studentName = student.getFirstName();
            this.courseNum = quiz.getCourseID();
            this.examName = quiz.getExamName();
            this.score = score;
            this.fullScore = quiz.getTotalScore();
            this.timeSpend = timeSpend;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getCourseNum() {
            return courseNum;
        }

        public String getExamName() {
            return examName;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getFullScore() {
            return fullScore;
        }

        public String getTimeSpend() {
            return timeSpend;
        }
    }

    @FXML
    private ChoiceBox<String> courseCombox;
    @FXML
    private ChoiceBox<String> examCombox;
    @FXML
    private ChoiceBox<String> studentCombox;
    @FXML
    private TableView<GradeExampleClass> gradeTable;
    @FXML
    private TableColumn<GradeExampleClass, String> studentColumn;
    @FXML
    private TableColumn<GradeExampleClass, String> courseColumn;
    @FXML
    private TableColumn<GradeExampleClass, String> examColumn;
    @FXML
    private TableColumn<GradeExampleClass, String> scoreColumn;
    @FXML
    private TableColumn<GradeExampleClass, String> fullScoreColumn;
    @FXML
    private TableColumn<GradeExampleClass, String> timeSpendColumn;
    @FXML
    BarChart<String, Number> barChart;
    @FXML
    CategoryAxis categoryAxisBar;
    @FXML
    NumberAxis numberAxisBar;
    @FXML
    LineChart<String, Number> lineChart;
    @FXML
    CategoryAxis categoryAxisLine;
    @FXML
    NumberAxis numberAxisLine;
    @FXML
    PieChart pieChart;
    private String teacher_name;
    private Teacher user;
    /**
     * Sets the teacher ID.
     *
     * @param teachername the name of the teacher.
     */
    public void setTeacherId(String teachername) {
        this.teacher_name = teachername;
    }
    private GradeExampleClass selectedEntry;

    private final ObservableList<GradeExampleClass> gradeList = FXCollections.observableArrayList();
    private List<Quiz> quizList;
    private List<Course> courseList;
    private ObservableList<String> studentChoices=FXCollections.observableArrayList();
    private ObservableList<String> examChoices=FXCollections.observableArrayList();
    private ObservableList<String> courseChoices=FXCollections.observableArrayList();
    /**
     * Handles the selection of an entry in the grade table.
     *
     * @param event the mouse event triggered by selecting an entry.
     */
    @FXML
    void selectEntry(MouseEvent event) {
        selectedEntry = gradeTable.getSelectionModel().getSelectedItem();
        loadChart();
    }
    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Database<Teacher> teacherDB = new Database<>(Teacher.class);
        Optional<Teacher> optionalUser = teacherDB.queryByField("username", teacher_name).stream().findFirst();
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            showMsg("Teacher not found");
            return;
        }
        barChart.setAnimated(false);
        barChart.setLegendVisible(false);
        categoryAxisBar.setLabel("Course");
        numberAxisBar.setLabel("Avg. Score");
        pieChart.setLegendVisible(false);
        pieChart.setTitle("Student Scores");
        lineChart.setLegendVisible(false);
        categoryAxisLine.setLabel("Exam");
        numberAxisLine.setLabel("Avg. Score");

        gradeTable.setItems(gradeList);
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseNum"));
        examColumn.setCellValueFactory(new PropertyValueFactory<>("examName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        fullScoreColumn.setCellValueFactory(new PropertyValueFactory<>("fullScore"));
        timeSpendColumn.setCellValueFactory(new PropertyValueFactory<>("timeSpend"));
        if (!gradeList.isEmpty()) {
            selectedEntry = gradeList.get(0);
        } else {
            selectedEntry = null;
        }

        refresh();
        loadChart();
    }
    /**
     * Refreshes the data in the grade table and choice boxes.
     */
    @FXML
    public void refresh() {
        Database<Quiz> quizDB = new Database<>(Quiz.class);
        examChoices.clear();
        studentChoices.clear();
        courseChoices.clear();
        quizList = quizDB.queryByField("teachername",user.getUsername());
        for (Quiz quiz : quizList) {
            examChoices.add(quiz.getExamName());
        }
        Database<Student> studentDB = new Database<>(Student.class);
        courseList = FXCollections.observableArrayList(new Database<>(Course.class).queryByField("department", user.getDepartment()));
        for (Course course : courseList) {
            courseChoices.add(course.getCourseID());
        }

        gradeList.clear();
        for (Quiz quiz : quizList) {
            List<String> students = quiz.getStudents();
            List<String> scores = quiz.getScores();
            List<String> timeSpent = quiz.getTimeSpent();
            System.out.println(students);
            System.out.println(scores);
            System.out.println(timeSpent);
            for (int i = 0; i < students.size(); i++) {
                String studentIdStr = students.get(i);
                Student student = studentDB.queryByKey(studentIdStr);
                if (student == null) continue;

                if (!studentChoices.contains(student.getFirstName())){
                    studentChoices.add(student.getFirstName());
                }
                gradeList.add(new GradeExampleClass(quiz, student, scores.get(i), timeSpent.get(i)));
            }
        }
        courseCombox.setItems(courseChoices);
        examCombox.setItems(examChoices);
        studentCombox.setItems(studentChoices);

        query();
    }
    /**
     * Load the chart
     */
    private void loadChart() {
        if (quizList.isEmpty()) {
            showMsg("No quiz found");
            return;
        }
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
        seriesBar.getData().clear();

        for (Course course : courseList) {
            seriesBar.getData().add(new XYChart.Data<>(course.getCourseName(), course.getAverageScore()));
        }
        barChart.getData().clear();

        barChart.getData().add(seriesBar);


        pieChart.getData().clear();
        if (selectedEntry == null) {
            return;
        }
        String exam = selectedEntry.getExamName();
        Database<Quiz> quizDB = new Database<>(Quiz.class);
        Database<Student> studentDB = new Database<>(Student.class);
        Optional<Quiz> optionalQuiz = quizDB.queryByField("examName", exam).stream().findFirst();
        if (optionalQuiz.isPresent()) {
            Quiz selectedQuiz = optionalQuiz.get();
            for (int i = 0; i < selectedQuiz.getStudents().size(); i++) {
                Student student = studentDB.queryByKey(selectedQuiz.getStudents().get(i));
                if (student == null) continue;
                pieChart.getData().add(new PieChart.Data(student.getFirstName(),Double.parseDouble(selectedQuiz.getScores().get(i))));
            }
        } else {
            showMsg("No quiz found for the selected exam");
        }

        XYChart.Series<String, Number> seriesLine = new XYChart.Series<>();
        seriesLine.getData().clear();
        lineChart.getData().clear();
        for (Quiz quiz : quizList) {
            seriesLine.getData().add(new XYChart.Data<>(quiz.getCourseID() + "-" + quiz.getExamName(), quiz.getMean()));
        }
        lineChart.getData().add(seriesLine);
    }
    /**
     * Resets the choice boxes and grade table.
     */
    @FXML
    public void reset() {
        courseCombox.getItems().clear();
        examCombox.getItems().clear();
        studentCombox.getItems().clear();
        gradeTable.setItems(gradeList);
        refresh();
    }

    /**
     * Queries the grade table based on the selected course, exam, and student.
     */
    @FXML
    public void query() {
        String course = courseCombox.getValue();
        String exam = examCombox.getValue();
        String student = studentCombox.getValue();

        FilteredList<GradeExampleClass> filteredData = new FilteredList<>(gradeList, p -> true);

        filteredData.setPredicate(grade -> {
            if (course != null && !course.isEmpty() && !grade.getCourseNum().equals(course)) {
                return false;
            }
            if (exam != null && !exam.isEmpty() && !grade.getExamName().equals(exam)) {
                return false;
            }
            if (student != null && !student.isEmpty() && !grade.getStudentName().equals(student)) {
                return false;
            }
            return true;
        });

        gradeTable.setItems(filteredData);
    }
}