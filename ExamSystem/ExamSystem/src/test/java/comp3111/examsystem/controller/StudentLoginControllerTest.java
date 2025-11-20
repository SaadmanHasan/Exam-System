package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the StudentLoginController.
 */
class StudentLoginControllerTest {

    private StudentLoginController controller;
    private Database<Student> manageStudentDatabase = new Database<>(Student.class);

    /**
     * @throws InterruptedException if the JavaFX platform does not start
     */
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

    /**
     * @throws IOException if the FXMLLoader cannot load the FXML file
     */
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentLoginUI.fxml"));
                fxmlLoader.load();
                controller = fxmlLoader.getController();
                controller.passwordTxt = new PasswordField();
                controller.usernameTxt = new TextField();
                latch.countDown();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("FXMLLoader did not complete in time");
        }
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
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Platform.runLater did not complete in time");
        }
    }

    /**
     * Test if all fields are empty
     */
    @Test
    public void StudentLogin_checkIfAllEmpty() throws InterruptedException {
        controller.usernameTxt.setText("");
        controller.passwordTxt.setText("");

        runAndWait(() -> {
            try {
                controller.login(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(controller.checkIfAllEmpty());
    }

    /**
     * Test student can login with correct username and password
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    public void StudentLogin_correctLogin() throws InterruptedException {
        controller.usernameTxt.setText("Aarav");
        controller.passwordTxt.setText("hi");

        runAndWait(() -> {
            try {
                controller.login(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(controller.validate());
    }

    /**
     * Test student cannot login with incorrect username and password
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    public void StudentLogin_incorrectLogin() throws InterruptedException {
        controller.usernameTxt.setText("NoWayThisUsernameWillExist");
        controller.passwordTxt.setText("YeahNoWayThisPasswordWillExist");

        runAndWait(() -> {
            try {
                controller.login(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertFalse(controller.validate());
    }

    /**
     * Test student cannot login with an empty username
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    public void StudentLogin_emptyUsername() throws InterruptedException {
        controller.usernameTxt.setText("");
        controller.passwordTxt.setText("password");

        runAndWait(() -> {
            try {
                controller.login(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertFalse(controller.validate());
    }

    /**
     * Test student cannot login with an empty password
     *
     * @throws InterruptedException if the JavaFX platform does not start
     */
    @Test
    public void StudentLogin_emptyPassword() throws InterruptedException {
        controller.usernameTxt.setText("username");
        controller.passwordTxt.setText("");

        runAndWait(() -> {
            try {
                controller.login(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertFalse(controller.validate());
    }

}