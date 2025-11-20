package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Question;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ExamManageControllerTest {
    private ExamManageController controller;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX platform failed to start");
        }
    }
    @BeforeEach
    void setUp() throws IOException {
        System.out.println("Setting up");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ExamManage.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.name_publish = new TextField();
        controller.time_publish = new TextField();
        controller.publish = new javafx.scene.control.ComboBox<>();

    }
    private void runAndWait(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Platform.runLater did not complete in time");
        }
    }

    @Test
    void Test_exam_name() throws InterruptedException{
        controller.name_publish.setText("");
        controller.time_publish.setText("5");
        runAndWait(() -> controller.add(null));
        assertTrue(controller.QuizDB.queryByField("examName", "").isEmpty());
    }

    @Test
    void Test_exam_time() throws InterruptedException{
        controller.name_publish.setText("Quiz_test");
        controller.time_publish.setText("");
        runAndWait(() -> controller.add(null));
        assertTrue(controller.QuizDB.queryByField("examName", "Quiz_test").isEmpty());
    }

    @Test
    void Test_add_exam() throws InterruptedException{
        controller.name_publish.setText("Quiz_test1");
        controller.time_publish.setText("51");
        controller.teacher_name = "dshasan";
        Database<Question> qdb = new Database<>(Question.class);
        controller.Curr_questions = qdb.queryByField("teachername", controller.teacher_name);
        runAndWait(() -> controller.add(null));
        Thread.sleep(100);
        Database<Quiz> qdb2 = new Database<>(Quiz.class);
        assertFalse(qdb2.queryByField("examName", "Quiz_test1").isEmpty());
    }

    @Test
    void Test_update_name()throws InterruptedException{
        controller.name_publish.setText("");
        controller.time_publish.setText("5");
        runAndWait(() -> controller.update(null));
        assertTrue(controller.QuizDB.queryByField("examName", "").isEmpty());
    }
    @Test
    void Test_update_time()throws InterruptedException{
        controller.name_publish.setText("Quiz_test2");
        controller.time_publish.setText("");
        runAndWait(() -> controller.update(null));
        assertTrue(controller.QuizDB.queryByField("examName", "Quiz_test2").isEmpty());
    }

    @Test
    void Test_update_exam_no_exist() throws InterruptedException{
        controller.publish.setValue("No");
        controller.name_publish.setText("Quiz_test_null");
        controller.time_publish.setText("5");
        runAndWait(() -> controller.update(null));
        assertTrue(controller.QuizDB.queryByField("examName", "Quiz_test_null").isEmpty());
    }
    @Test
    void Test_update_exam_published() throws InterruptedException{
        controller.publish.setValue("Yes");
        controller.name_publish.setText("Quiz 3");
        controller.time_publish.setText("5");
        controller.teacher_name = "dshasan";
        Database<Question> qdb = new Database<>(Question.class);
        controller.Curr_questions = qdb.queryByField("teachername", controller.teacher_name);
        runAndWait(() -> controller.update(null));
        Thread.sleep(100);
        Database<Quiz> qdb2 = new Database<>(Quiz.class);
        assertFalse(qdb2.queryByField("examName", "Quiz 3").isEmpty());
    }
    @Test
    void Test_update() throws InterruptedException{
        controller.publish.setValue("No");
        controller.name_publish.setText("Quiz 3");
        controller.time_publish.setText("5");
        controller.teacher_name = "dshasan";
        controller.course_id.setValue("COMP3112");
        Database<Question> qdb = new Database<>(Question.class);
        controller.Curr_questions = qdb.queryByField("teachername", controller.teacher_name);
        System.out.println(controller.Curr_questions.size());
        runAndWait(() -> controller.update(null));
        Thread.sleep(100);
        Database<Quiz> qdb2 = new Database<>(Quiz.class);
        assertFalse(qdb2.queryByField("examTime", "5").isEmpty());
    }




}