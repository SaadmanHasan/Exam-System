package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Question;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class QuestionBankControllerTest {
    private QuestionBankController controller;

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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("QuestionBank.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.input_question = new TextField();
        controller.input_score = new TextField();
        controller.option_a = new TextField();
        controller.option_b = new TextField();
        controller.option_c = new TextField();
        controller.answer = new TextField();
        controller.score = new TextField();
        controller.question = new TextField();

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
    void Test_input_field_score()throws InterruptedException{
        controller.question.setText("What if");
        controller.option_a.setText("Paris");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "What if").isEmpty());
    }
    @Test
    void Test_empty_input_question() throws InterruptedException {
        controller.question.setText("");
        controller.input_score.setText("5");
        controller.option_a.setText("Paris");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }
    @Test
    void Test_empty_input_option_a() throws InterruptedException {
        controller.question.setText("What is this");
        controller.input_score.setText("5");
        controller.option_a.setText("");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "What is this").isEmpty());
    }
    @Test
    void Test_empty_input_option_b() throws InterruptedException {
        controller.question.setText("What is this");
        controller.input_score.setText("5");
        controller.option_a.setText("d");
        controller.option_b.setText("");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "What is this").isEmpty());
    }
    @Test
    void Test_empty_input_option_c() throws InterruptedException {
        controller.question.setText("What is this");
        controller.input_score.setText("5");
        controller.option_a.setText("d");
        controller.option_b.setText("sd");
        controller.option_c.setText("");
        controller.answer.setText("Paris");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "What is this").isEmpty());
    }
    @Test
    void Test_empty_input_answer() throws InterruptedException {
        controller.question.setText("What is this");
        controller.input_score.setText("5");
        controller.option_a.setText("d");
        controller.option_b.setText("sd");
        controller.option_c.setText("ds");
        controller.answer.setText("");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "What is this").isEmpty());
    }

    @Test
    void Test_question_added() throws InterruptedException{
        controller.question.setText("What is the capital of France?");
        controller.input_score.setText("5");
        controller.option_a.setText("Paris");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("5");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        Thread.sleep(100);
        Database<Question> qdb = new Database<>(Question.class);
        assertFalse(qdb.queryByField("Question", "What is the capital of France?").isEmpty());
    }
    @Test
    void Test_input_field_question_update()throws InterruptedException{
        controller.question.setText("");
        controller.input_score.setText("5");
        controller.option_a.setText("Paris");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("ds");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.update(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }
    @Test
    void Test_input_field_option1_update()throws InterruptedException{
        controller.question.setText("dssdf");
        controller.input_score.setText("5");
        controller.option_a.setText("");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("ds");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.add(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }
    @Test
    void Test_input_field_option2_update()throws InterruptedException{
        controller.question.setText("dssdf");
        controller.input_score.setText("5");
        controller.option_a.setText("sd");
        controller.option_b.setText("");
        controller.option_c.setText("Berlin");
        controller.answer.setText("Paris");
        controller.score.setText("ds");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.update(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }
    @Test
    void Test_input_field_option3_update()throws InterruptedException{
        controller.question.setText("dssdf");
        controller.input_score.setText("5");
        controller.option_a.setText("as");
        controller.option_b.setText("London");
        controller.option_c.setText("");
        controller.answer.setText("Paris");
        controller.score.setText("ds");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.update(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }
    @Test
    void Test_input_field_answer_update()throws InterruptedException{
        controller.question.setText("dssdf");
        controller.input_score.setText("5");
        controller.option_a.setText("sd");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("");
        controller.score.setText("ds");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.update(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }
    @Test
    void Test_input_field_score_update()throws InterruptedException{
        controller.question.setText("dssdf");
        controller.input_score.setText("5");
        controller.option_a.setText("sd");
        controller.option_b.setText("London");
        controller.option_c.setText("Berlin");
        controller.answer.setText("sd");
        controller.score.setText("");
        controller.teacher_name = "dshasan";
        runAndWait(() -> controller.update(null));
        assertTrue(true);
        assertTrue(controller.QuestionDB.queryByField("Question", "").isEmpty());
    }

    @Test
    void testUpdate() throws InterruptedException {
        // Set up initial data
        Question initialQuestion = new Question(1L, "Initial Question", "A", "B", "C", "Single", "A", "5", "dshasan");
        controller.table = new TableView<>();
        controller.table.getItems().add(initialQuestion);

        // Select the item in the table
        runAndWait(() -> controller.table.getSelectionModel().select(initialQuestion));
        // Set new values in the text fields
        controller.question.setText("Updated Question");
        controller.option_a.setText("X");
        controller.option_b.setText("Y");
        controller.option_c.setText("Z");
        controller.answer.setText("X");
        controller.score.setText("10");
        controller.teacher_name = "dshasan";
        // Call the update method
        runAndWait(() -> controller.update(null));

        Thread.sleep(1000);
        // Verify the update
        Database<Question> qdb = new Database<>(Question.class);
        List<Question> updatedQuestions = qdb.queryByField("Question", "Updated Question");
        assertEquals(1, updatedQuestions.size());
    }

    
}