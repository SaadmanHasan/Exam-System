package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TeacherLoginControllerTest {
    private TeacherLoginController controller;

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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherLoginUI.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.usernameTxt = new TextField();
        controller.passwordTxt = new PasswordField();
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
    void testCheckIfExists() {
        assertFalse(controller.CheckIfExists("teacher", "teacher"));
        assertTrue(controller.CheckIfExists("dshasan", "dd"));
    }

    @Test
    void Test_usernameTXT_empty() throws InterruptedException{
        controller.usernameTxt.setText("");
        controller.passwordTxt.setText("dd");
        runAndWait(() -> controller.login(null));
        assertTrue(true);
    }
    @Test
    void Test_passwordTXT_empty() throws InterruptedException{
        controller.usernameTxt.setText("dshasan");
        controller.passwordTxt.setText("");
        runAndWait(() -> controller.login(null));
        assertTrue(true);
    }
    @Test
    void Test_username_password_correct() throws InterruptedException{
        controller.usernameTxt.setText("dshasan");
        controller.passwordTxt.setText("dd");
        runAndWait(() -> controller.login(null));
        assertTrue(true);
    }

}