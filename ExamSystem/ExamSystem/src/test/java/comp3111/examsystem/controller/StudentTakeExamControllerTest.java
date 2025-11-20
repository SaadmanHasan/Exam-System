package comp3111.examsystem.controller;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the StudentTakeExamController.
 */
class StudentTakeExamControllerTest {

    private StudentTakeExamController controller;

    //gets the first Quiz from the DB
    private final Database<Quiz> quizDatabase = new Database<>(Quiz.class);
    private final Quiz sampleQuiz = quizDatabase.getAll().getFirst();

    //Create a sample student
    private static Student sampleStudent = new Student(1L, "Sample Student For Take Exam Testing", "password", "Sample Student", "20", "Computer Science", "Male");

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        if (!Platform.isFxApplicationThread() && !Platform.isImplicitExit()) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("JavaFX platform failed to start");
            }
        }
    }

    @BeforeEach
    void setUp() throws Exception, IOException {
        System.out.println("Setting up");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentTakeExam.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == StudentTakeExamController.class) {
                StudentTakeExamController controller = new StudentTakeExamController();
                controller.setStudentName(sampleStudent.getUsername());
                controller.setQuizName(sampleQuiz.getExamName());
                controller.setThisQuiz(sampleQuiz);
                return controller;
            } else {
                try {
                    return controllerClass.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        fxmlLoader.load();
        controller = fxmlLoader.getController();

        //Add the sample student to the DB
        Database<Student> studentDatabase = new Database<>(Student.class);
        studentDatabase.add(sampleStudent);

        controller.initialize(null, null);
    }

    /**
     * Helper function
     * @param action the action to run on the JavaFX thread
     * @throws InterruptedException if the Platform.runLater does not complete in time
     */
    private void runAndWait(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Platform.runLater did not complete in time");
        }
    }

    /**
     * Initialize the controller and check if the quiz and student name are set correctly
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    void testInitialize() throws InterruptedException {
        assertNotNull(controller.getThisQuiz());
        assertEquals(sampleQuiz.getExamName(), controller.getQuizName());
        assertEquals(sampleStudent.getUsername(), controller.getStudentName());
    }

    /**
     * Check if the updateTime function works or not
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    void testUpdateTime() throws InterruptedException {
        runAndWait(() -> controller.updateTime());
        assertNotNull(controller.remainingTime.getText());
    }

    /**
     * Tests if the submit button adds the student to the quiz, adds the score, and adds the time spent
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    void SubmitQuiz_NoAnswer() throws InterruptedException {

        Platform.runLater(() -> {
            controller.submitQuiz(new ActionEvent(new Button(), null));
        });

        Platform.runLater(() -> {
            assertTrue(sampleQuiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            int startIndex = sampleQuiz.getStudents().indexOf(String.valueOf(sampleStudent.id()));

            //Check if the student's score is added
            assertEquals("0", sampleQuiz.getScores().get(startIndex));

            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            Quiz quiz = quizDatabase.getAll().getFirst();

            assertTrue(quiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            assertEquals("0", quiz.getScores().get(startIndex));
        });
    }

    /**
     * Tests if the submit button adds the student to the quiz, adds the score, and adds the time spent.
     * Only Wrong ans are submitted, so score is "0"
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    void SubmitQuiz_WrongAnswers() throws InterruptedException {
        Platform.runLater(() -> {

            List<String> dummyAnswers = new ArrayList<>();
            for (int i = 0; i < sampleQuiz.getQuestions().size(); i++) {
                dummyAnswers.add("X");
            }

            controller.selectedAnswers = dummyAnswers;

            controller.submitQuiz(new ActionEvent(new Button(), null));
        });

        Platform.runLater(() -> {
            assertTrue(sampleQuiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            int startIndex = sampleQuiz.getStudents().indexOf(String.valueOf(sampleStudent.id()));

            //Check if the student's score is added
            assertEquals("0", sampleQuiz.getScores().get(startIndex));

            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            Quiz quiz = quizDatabase.getAll().getFirst();

            assertTrue(quiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            assertEquals("0", quiz.getScores().get(startIndex));
        });
    }

    /**
     * Tests if the submit button adds the student to the quiz, adds the score, and adds the time spent.
     * All correct answers are submitted, so score is the max score possible for this sampleQuiz
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    void SubmitQuiz_RightAnswers() throws InterruptedException {
        Platform.runLater(() -> {

            List<String> dummyAnswers = new ArrayList<>();
            for (int i = 0; i < sampleQuiz.getQuestions().size(); i++) {
                dummyAnswers.add(String.valueOf(controller.getCorrectAnswers().getFirst()));
            }

            controller.selectedAnswers = dummyAnswers;

            controller.submitQuiz(new ActionEvent(new Button(), null));
        });

        Platform.runLater(() -> {
            assertTrue(sampleQuiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            int startIndex = sampleQuiz.getStudents().indexOf(String.valueOf(sampleStudent.id()));

            //Check if the student's score is added
            assertEquals(sampleQuiz.getTotalScore(), sampleQuiz.getScores().get(startIndex));

            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            Quiz quiz = quizDatabase.getAll().getFirst();

            assertTrue(quiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            assertEquals(quiz.getTotalScore(), quiz.getScores().get(startIndex));
        });
    }

    /**
     * Tests if the submit button adds the student to the quiz, adds the score, and adds the time spent.
     * Time spent is 1 minute
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    void SubmitQuiz_TimeSpent() throws InterruptedException {
        Platform.runLater(() -> {

            controller.endTime = System.currentTimeMillis() + 60000;

            controller.submitQuiz(new ActionEvent(new Button(), null));
        });

        Platform.runLater(() -> {
            assertTrue(sampleQuiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            int startIndex = sampleQuiz.getStudents().indexOf(String.valueOf(sampleStudent.id()));

            //Check if the student's score is added
            assertEquals(sampleQuiz.getTotalScore(), sampleQuiz.getScores().get(startIndex));
            assertEquals("1", sampleQuiz.getTimeSpent().get(startIndex));

            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            Quiz quiz = quizDatabase.getAll().getFirst();

            assertTrue(quiz.getStudents().contains(String.valueOf(sampleStudent.id())));
            assertEquals(quiz.getTotalScore(), quiz.getScores().get(startIndex));

            assertEquals("1", quiz.getTimeSpent().get(startIndex));
        });
    }

    /**
     * Removes the sample student from the DB of Quiz and Student
     */
    @AfterEach
    void tearDown() {
        //Remove the sample student from the DB
        Database<Student> studentDatabase = new Database<>(Student.class);
        studentDatabase.delByFiled("username",sampleStudent.getUsername());

        if (controller.getThisQuiz().getStudents().contains(String.valueOf(sampleStudent.id()))) {
            int index = sampleQuiz.getStudents().indexOf(String.valueOf(sampleStudent.id()));
            //removing from everywhere
            sampleQuiz.getTimeSpent().remove(index);
            sampleQuiz.getStudents().remove(index);
            sampleQuiz.getScores().remove(index);
            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            quizDatabase.update(sampleQuiz);
            System.out.println("Removed the student from the quiz");
        }
    }

}