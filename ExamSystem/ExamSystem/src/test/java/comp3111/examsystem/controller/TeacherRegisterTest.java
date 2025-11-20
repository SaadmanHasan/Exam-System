package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TeacherRegisterTest {
    private TeacherRegister controller;

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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherRegister.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.username = new TextField();
        controller.name = new TextField();
        controller.age = new TextField();
        controller.department = new TextField();
        controller.password = new PasswordField();
        controller.confirm_password = new PasswordField();
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
    void Invalid_username() throws InterruptedException {
        controller.username.setText("dshasan");
        controller.name.setText("123");
        controller.age.setText("123");
        controller.department.setText("123");
        controller.password.setText("123");
        controller.confirm_password.setText("123");
        runAndWait(() -> controller.register(null));
        List<Teacher> teachers = controller.teacherDB.queryByField("username", "dshasan");
        Teacher teacher = teachers.getFirst();
        assertEquals("dshasan", teacher.getUsername());
    }

    @Test
    void Invalid_age() throws InterruptedException{
        controller.username.setText("");
        controller.name.setText("123");
        controller.age.setText("123");
        controller.department.setText("123");
        controller.password.setText("123");
        controller.confirm_password.setText("123");
        runAndWait(() -> controller.register(null));
        List<Teacher> teachers = controller.teacherDB.queryByField("username", "");
        assertTrue(teachers.isEmpty());
    }
    @Test
    void Invalid_department() throws InterruptedException{
        controller.username.setText("aasdf");
        controller.name.setText("123");
        controller.age.setText("123");
        controller.department.setText("");
        controller.password.setText("123");
        controller.confirm_password.setText("123");
        runAndWait(() -> controller.register(null));
        List<Teacher> teachers = controller.teacherDB.queryByField("username", "");
        assertTrue(teachers.isEmpty());
    }

    @Test
    void Test_inputage() throws InterruptedException{
        controller.username.setText("ads");
        controller.name.setText("123");
        controller.age.setText("123");
        controller.department.setText("123");
        controller.password.setText("12");
        controller.confirm_password.setText("12");
        runAndWait(() -> controller.register(null));
        assertTrue(controller.teacherDB.queryByField("username", "ads").isEmpty());
    }
    @Test
    void Test_negative_age() throws InterruptedException{
        controller.username.setText("ads");
        controller.name.setText("123");
        controller.age.setText("-123");
        controller.department.setText("123");
        controller.password.setText("12");
        controller.confirm_password.setText("12");
        runAndWait(() -> controller.register(null));
        assertTrue(controller.teacherDB.queryByField("username", "ads").isEmpty());
    }
    @Test
    void invalid_password()throws InterruptedException{
        controller.username.setText("ads");
        controller.name.setText("123");
        controller.age.setText("22");
        controller.department.setText("123");
        controller.password.setText("12");
        controller.confirm_password.setText("123");
        runAndWait(() -> controller.register(null));
        assertTrue(controller.teacherDB.queryByField("username", "ads").isEmpty());
    }
    @Test
    void Test_intergerage()throws InterruptedException{
        controller.username.setText("ads");
        controller.name.setText("123");
        controller.age.setText("sdfs");
        controller.department.setText("123");
        controller.password.setText("12");
        controller.confirm_password.setText("123");
        runAndWait(() -> controller.register(null));
        assertTrue(controller.teacherDB.queryByField("username", "ads").isEmpty());
    }
}
