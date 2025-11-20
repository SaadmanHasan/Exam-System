//Aarav
package comp3111.examsystem.controller;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import comp3111.examsystem.Classes.Question;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Controller class for handling the student exam-taking functionality.
 * This class is responsible for managing the exam UI, handling user interactions,
 * and processing the exam data.
 */
public class StudentTakeExamController implements Initializable {

    @FXML
    private Label CourseCode_ExamName;

    @FXML
    private RadioButton option1;

    @FXML
    private RadioButton option2;

    @FXML
    private RadioButton option3;

    @FXML
    private TableView<Question> questionList;

    @FXML
    private Label questionNumber;

    @FXML
    private TableColumn<Question, String> questionList_col;

    @FXML
    private TextArea questionText;

    @FXML
    public Label remainingTime;

    @FXML
    private Label totalQuestions;

    @FXML
    private ToggleGroup RadioButtons_ToggleGroup;

    @FXML
    private Button submitButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    //
    //Custom Data Members
    //

    private Long startTime;

    public Long endTime;

    public Boolean isQuizSubmitted = false;

    //here the student is the student taking the exam
    private String studentName;

    //Contains all the questions
    private ObservableList<Question> observableQuestions;

    private Integer totalMins;

    //List of SelectedAnswers
    public List<String> selectedAnswers = new ArrayList<>();
    private List<Integer> selectedAnsweroption = new ArrayList<>();

    //Quiz will have all the questions needed for us
    private String quizName;

    private List<Question> allQuestions;

    private List<List<String>> correctAnswers = new ArrayList<>();

    private Timeline timeline;

    private Integer currentIndexCounter = 0;

    public Quiz thisQuiz;

    //
    //Helper functions
    //

    /**
     * Initializes the total minutes and start time for the quiz.
     */
    private void initializeTotalMinsAndStartTime() {
        Quiz quizObject = getThisQuiz();
        if (quizObject == null) {
            System.out.println("Quiz Doesnt exist");
            return;
        }
        this.totalMins = Integer.parseInt(quizObject.getExamTime());
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Retrieves the quiz object based on the quiz name.
     *
     * @return the quiz object if found, otherwise null
     */
    private Quiz getQuiz() {
        Database<Quiz> quizDatabase = new Database<>(Quiz.class);
        List<Quiz> allQuiz = quizDatabase.getAll();
        for (Quiz quiz: allQuiz) {
            if (Objects.equals(quiz.getExamName(), quizName)) { return quiz;}
        }
        //If quiz not found
        return null;
    }


    /**
     * Retrieves the student object based on the student name.
     *
     * @return the student object if found, otherwise null
     */
    private Student getStudent() {
        Database<Student> studentDatabase = new Database<>(Student.class);
        List<Student> allStudents = studentDatabase.getAll();
        for (Student student: allStudents) {
            if (Objects.equals(student.getUsername(), studentName)) { return student;}
        }
        //If student not found
        return null;
    }


    /**
     * Retrieves the list of questions based on their IDs.
     *
     * @param questions the list of question IDs
     * @return the list of question objects
     */
    private List<Question> getQuestions(List<Long> questions) {
        Database<Question> questionDatabase = new Database<>(Question.class);
        List<Question> res_question = new ArrayList<>();

        for (Long questionID: questions) {
            List<Question> q = questionDatabase.queryByField("id", String.valueOf(questionID));
            res_question.addAll(q);
        }

        return res_question;
    }

    /**
     * Updates the question text area with the selected question's text.
     */
    private void updateTextArea() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            questionText.setText(selectedQuestion.getQuestion()); // Update TextArea with question text
        } else {
            questionText.clear(); // Clear TextArea if no selection
        }
    }

    /**
     * Updates the radio buttons with the selected question's options.
     */
    private void updateOptionsText() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            option1.setText(selectedQuestion.getOption1());
            option2.setText(selectedQuestion.getOption2());
            option3.setText(selectedQuestion.getOption3());
        } else {
            option1.setText("");
            option2.setText("");
            option3.setText("");
        }
    }

    /**
     * Updates the selected option for the current question.
     */
    private void updateOptionsChoice() {
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Integer index = getSelectedQuestionIndex();
            Integer option = getSelectedAnsweroption().get(index);
            if (option == 1) {
                option1.setSelected(true);
            } else if (option == 2) {
                option2.setSelected(true);
            } else if (option == 3) {
                option3.setSelected(true);
            } else {
                option1.setSelected(false);
                option2.setSelected(false);
                option3.setSelected(false);
            }
        }

    }

    /**
     * Updates the selected answer for the current question.
     */
    private void updateSelectedAns() {
        //This is to keep track of the selected option
        if (option1.isSelected()) {
            selectedAnswers.set(getSelectedQuestionIndex(),"A");
            selectedAnsweroption.set(getSelectedQuestionIndex(), 1);
        } else if (option2.isSelected()) {
            selectedAnswers.set(getSelectedQuestionIndex(),"B");
            selectedAnsweroption.set(getSelectedQuestionIndex(), 2);
        } else if (option3.isSelected()) {
            selectedAnswers.set(getSelectedQuestionIndex(),"C");
            selectedAnsweroption.set(getSelectedQuestionIndex(), 3);
        }
    }

    /**
     * Retrieves the index of the selected question.
     *
     * @return the index of the selected question
     */
    public Integer getSelectedQuestionIndex() {
        // Get selected question
        Question selectedQuestion = questionList.getSelectionModel().getSelectedItem();
        Integer index = null;
        if (selectedQuestion != null) {
            // Find index of selected question
            index = observableQuestions.indexOf(selectedQuestion);
        }
        return index;
    }


    /**
     * Initializes the selected answers list.
     */
    private void initializingSelectedAns() {
        for (int i = 0; i < allQuestions.size(); i++) {
            this.selectedAnswers.add("");
            this.selectedAnsweroption.add(-1);
        }
    }

    /**
     * Initializes the correct answers list.
     */
    private void initializingCorrectAns() {

        //List of correct answers --> in the format: [A, B, C, AB, BC, C] --> want to change to [[A], [A,B]]
        List<String> correctAns = allQuestions.stream().map(Question::getAnswer).toList();
        for (String ans : correctAns) {
            List<String> ansList = new ArrayList<>();
            for (int i = 0; i < ans.length(); i++) {
                ansList.add(String.valueOf(ans.charAt(i)));
            }
            this.correctAnswers.add(ansList);
        }
    }

    /**
     * Updates the remaining time label.
     */
    public void updateTime() {
        //This is to update the time
        Long elapsedTime = (System.currentTimeMillis() - getStartTime()) / (1000 * 60);
        Long remainingTime_long = getTotalMins() - elapsedTime;
        remainingTime.setText("Remaining Time: " + (getTotalMins() - ((System.currentTimeMillis()-getStartTime())/ (1000 * 60)) + " mins"));

        if (remainingTime_long <= 0 && !this.isQuizSubmitted) {
            timeline.stop();
            submitQuiz(new ActionEvent());
        }
    }

    /**
     * Converts a list of strings to a list of longs.
     *
     * @param stringList the list of strings
     * @return the list of longs
     */
    private List<Long> convertStringToLong(List<String> stringList) {
        List<Long> longList = new ArrayList<>();
        for (String s : stringList) {
            longList.add(Long.parseLong(s));
        }
        return longList;
    }

    /**
     * Initializes the controller.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources the resources used to localize the root object, or null if the root object was not localized
     */
    public void initialize(URL location, ResourceBundle resources) {

        //QuestionText is Display only
        questionText.setEditable(false);

        //Disable the submit button
        this.submitButton.setDisable(true);

        //CourseCode_ExamName is the H1
        this.CourseCode_ExamName.setText(quizName);

        //Check if quiz object exists or not. Ideally, quiz should always exist
        Quiz quizObject = getThisQuiz();
        if (quizObject == null) {
            System.out.println("Quiz Doesnt exist");
            return;
        }

        //Getting all the questions and setting those values to the table column
        List<Long> allQuestionsID = convertStringToLong(quizObject.getQuestions());
        this.allQuestions = this.getQuestions(allQuestionsID);
        this.observableQuestions = FXCollections.observableArrayList(allQuestions);
        questionList_col.setCellValueFactory(new PropertyValueFactory<>("Question"));
        questionList.setItems(observableQuestions);
        questionList.setDisable(true);

        //This will hold the selected options for every question. Index maps to question, value at index maps to the option
        initializingSelectedAns();
        initializingCorrectAns();
        initializeTotalMinsAndStartTime();

        //Initializing the value of totalQuestions
        totalQuestions.setText("Total Questions: " + String.valueOf(allQuestions.size()));

        //Taking care of the time
        // Initialize and start the timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> this.updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //remainingTime.setText("Remaining Time: " + (getTotalMins() - ((System.currentTimeMillis()-getStartTime())/ (1000 * 60)) + " mins"));
        //System.out.println((getTotalMins() - ((System.currentTimeMillis()-getStartTime())/ (1000 * 60))));  //This is the time remaining

        //Toggle all options so only one of them can be selected at a time
        RadioButtons_ToggleGroup = new ToggleGroup();
        option1.setToggleGroup(RadioButtons_ToggleGroup);
        option2.setToggleGroup(RadioButtons_ToggleGroup);
        option3.setToggleGroup(RadioButtons_ToggleGroup);

        //Default Value, of the first item in the observableQuestions if not empty
        if (!observableQuestions.isEmpty()) {
            questionList.getSelectionModel().selectFirst(); // Selects the first row
            updateTextArea();
            updateOptionsText();
        }
    }


    /**
     * Handles the action of moving to the next question.
     *
     * @param event the action event
     */
    @FXML
    void nextQuestion(ActionEvent event) {
        if (currentIndexCounter < this.observableQuestions.size()) {
            updateSelectedAns();
            questionList.getSelectionModel().selectNext();
            updateTextArea();
            updateOptionsText();
            updateOptionsChoice();
        }

        //Taking care of the submitButton
        if (currentIndexCounter != this.observableQuestions.size()) {
            currentIndexCounter += 1;
        }
        if (currentIndexCounter >= this.observableQuestions.size()) {
            this.submitButton.setDisable(false);
        }
        //System.out.println("This is the SystemIndex:  " + currentIndex + "\tThis is the Counter: " + currentIndexCounter);
    }


    /**
     * Handles the action of moving to the previous question.
     *
     * @param event the action event
     */
    @FXML
    void previousQuestion(ActionEvent event) {
        if (currentIndexCounter >= 0) {
                //Store this ans
                updateSelectedAns();
                //Selecting the prev question
                questionList.getSelectionModel().selectPrevious();
                //Due Diligence
                updateTextArea();
                updateOptionsText();
                updateOptionsChoice();
        }

        //Taking care of the submitButton
        if (currentIndexCounter == this.observableQuestions.size()) {
            currentIndexCounter -= 2;
        }
        else if (currentIndexCounter > 0) {
            currentIndexCounter -= 1;
        }
        if (currentIndexCounter != this.observableQuestions.size()) {
            this.submitButton.setDisable(true);
        }
        //System.out.println("This is the SystemIndex:  " + currentIndex + "\tThis is the Counter: " + currentIndexCounter);
    }

    /**
     * Submits this quiz and reflects this in the database
     *
     * @param e the event that triggered the action
     */
    @FXML
    void submitQuiz(ActionEvent e) {

        this.isQuizSubmitted = true;
        this.endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        long timeTakenMinutes = timeTaken / (1000 * 60);

        //Getting all the correctAns made by the student
        Integer correctAnsCounter = 0;
        Integer score = 0;

        for (int i = 0; i < this.correctAnswers.size(); i++) {
            List<String> correctAns = this.correctAnswers.get(i);
            String selectedAns = this.selectedAnswers.get(i);
            System.out.println("Testing: " + correctAns.get(0) + "\t" + selectedAns);
            if (correctAns.contains(selectedAns)) {
                correctAnsCounter++;
                score += Integer.parseInt(this.allQuestions.get(i).getScore());
            }
        }

        //For the pop up. To be clean
        Integer totalQuestions = correctAnswers.size();
        String fract_correct = String.valueOf(correctAnsCounter) + "/" + String.valueOf(totalQuestions) + "Correct,";
        float precision_float = ((float) correctAnsCounter / totalQuestions) * 100;
        String precision = String.valueOf(precision_float);
        String messsage = fract_correct + "The precision is: " + precision + "%." + "The score is: " + score;

        //Showing the message
        Platform.runLater(() -> MsgSender.showMsg(messsage));

        //adding it to this quiz
        Quiz quizObject = getThisQuiz();

        //To make the Time spent
        if (quizObject == null) {
            System.out.println("Quiz Doesnt exist");
            return;
        }
        quizObject.takeExam(String.valueOf(Objects.requireNonNull(getStudent()).getId()), String.valueOf(score), String.valueOf(timeTakenMinutes));

        //Continuing the flow
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
        if (e.getSource() instanceof Button) {
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        } else {
            // Handle other event sources if necessary
            System.out.println("Student Did not finish the quiz in time. Submitting all the answers till now");
        }
    }

    //
    //Helper function --> getters and setters
    //

    public Integer getTotalMins() {
        return this.totalMins;
    }

    public Long getStartTime() {return this.startTime;}

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public List<String> getSelectedAnswers() {
        return selectedAnswers;
    }

    public void setSelectedAnswers(List<String> selectedAnswers) {
        this.selectedAnswers = selectedAnswers;
    }

    public List<Integer> getSelectedAnsweroption() {
        return selectedAnsweroption;
    }

    public void setSelectedAnsweroption(List<Integer> selectedAnsweroption) {
        this.selectedAnsweroption = selectedAnsweroption;
    }

    public List<List<String>> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<List<String>> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getCurrentIndexCounter() {
        return currentIndexCounter;
    }

    public void setCurrentIndexCounter(Integer currentIndexCounter) {
        this.currentIndexCounter = currentIndexCounter;
    }

    public Quiz getThisQuiz() {
        return thisQuiz;
    }

    public void setThisQuiz(Quiz thisQuiz) {
        this.thisQuiz = thisQuiz;
    }
}
