package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Main;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

/**
 * Test class for the StudentRegister controller.
 */
public class StudentRegisterTest {

    private StudentRegister controller;

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
    void setUp() throws IOException {
        System.out.println("Setting up");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StudentRegister.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.username = new TextField();
        controller.name = new TextField();
        controller.age = new TextField();
        controller.password = new PasswordField();
        controller.confirm_password = new PasswordField();
        controller.department = new TextField();
        controller.gender = new ComboBox<>();

        //These two are default values of the system. Might remove later
        controller.gender.setItems(controller.genderlist);
        controller.gender.setValue("Male");
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
        if (!latch.await(30, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Platform.runLater did not complete in time");
        }
    }

    /**
     * @throws InterruptedException if the user's password field and confirm password field's value do not match.
     */
    @Test
    public void testRegister_WrongPasswords() throws InterruptedException{
        controller.username.setText("username");
        controller.name.setText("name");
        controller.age.setText("20");
        controller.password.setText("password");
        controller.confirm_password.setText("password_shouldNotMatch");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        //Check that the Student was not added to the DB by username
        Database<Student> manageStudents = new Database<>(Student.class);
        assertTrue(manageStudents.queryByField("username", "").isEmpty());
    }

    /**
     * @throws InterruptedException if the user does not enter all fields.
     */
    @Test
    public void testRegister_MissingFields() throws InterruptedException{
        controller.username.setText("username");
        controller.name.setText("");
        controller.age.setText("");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        //Check that the Student was not added to the DB by username
        Database<Student> manageStudents = new Database<>(Student.class);
        assertTrue(manageStudents.queryByField("username", "").isEmpty());
    }

    /**
     * Checks for the right range of the Age entered by the user. Entered age in this case is: 5
     *
     * @throws InterruptedException if the Platform.runLater does not complete in time
     */
    @Test
    public void testRegister_WrongAgeRangeLowerRange() throws InterruptedException{
        controller.username.setText("username");
        controller.name.setText("Testing");
        controller.age.setText("5");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        //Check that the Student was not added to the DB by username
        Database<Student> manageStudents = new Database<>(Student.class);
        assertTrue(manageStudents.queryByField("username", "").isEmpty());

    }

    /**
     * Checks for the right range of the Age entered by the user. Entered age in this case is: 101
     *
     * @throws InterruptedException if the Platform.runLater does not complete in time
     */
    @Test
    public void testRegister_WrongAgeRangeUpperRange() throws IOException, InterruptedException{
        controller.username.setText("username");
        controller.name.setText("Testing");
        controller.age.setText("101");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> {
            ActionEvent mockEvent = new ActionEvent(new Button(), null);
            controller.register(null);
        });

        //Check that the Student was not added to the DB by username
        Database<Student> manageStudents = new Database<>(Student.class);
        assertTrue(manageStudents.queryByField("username", "").isEmpty());
    }

    /**
     * Checks for the right format of the Age entered by the user. Entered age in this case is: NotAnInteger
     *
     * @throws InterruptedException if the Platform.runLater does not complete in time
     */
    @Test
    public void testRegister_WrongAgeFormat() throws IOException, InterruptedException{
        controller.username.setText("username");
        controller.name.setText("Testing");
        controller.age.setText("NotAnInteger");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        runAndWait(() -> {
            //Check that the Student was not added to the DB by username
            Database<Student> manageStudents = new Database<>(Student.class);
            assertTrue(manageStudents.queryByField("username", "").isEmpty());
        });
    }

    /**
     * Checks whether the user is able to register with an existing username. Should not be able to register.
     *
     * @throws InterruptedException if the user tries to register with an existing username
     */
    @Test
    public void testRegister_ExistingUsername() throws InterruptedException{
        controller.username.setText("ExistingUsernameTest");
        controller.name.setText("Testing");
        controller.age.setText("21");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        runAndWait(() -> {
            //Check that the Student was added to the DB by username
            Database<Student> manageStudents = new Database<>(Student.class);
            assertTrue(!manageStudents.queryByField("username", "ExistingUsernameTest").isEmpty());
        });

        //Try to register with the same username
        runAndWait(() -> controller.register(null));

        runAndWait(() -> {
            Database<Student> manageStudents = new Database<>(Student.class);
            //Check that the Student was not added to the DB by username
            List<Student> students = manageStudents.queryByField("username", "ExistingUsernameTest");
            assertTrue(students.size() == 1);

            //delete this student for further use
            manageStudents.delByFiled("username", "ExistingUsernameTest");
        });

    }

    /**
     * Checks whether the user is able to register with the correct details. Should be able to register.
     *
     * @throws InterruptedException if the user is not able to register with the correct details
     */
    // Test for valid age range (upper bound)
    @Test
    public void testRegister_ValidAgeUpperBound() throws InterruptedException {
        controller.username.setText("validAgeUpperBoundTest");
        controller.name.setText("Testing");
        controller.age.setText("100");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        runAndWait(() -> {
            // Check that the Student was added to the DB by username
            Database<Student> manageStudents = new Database<>(Student.class);
            assertTrue(!manageStudents.queryByField("username", "validAgeUpperBoundTest").isEmpty());

            //delete this student for further use
            manageStudents.delByFiled("username", "validAgeUpperBoundTest");
        });
    }

    /**
     * Checks whether the user is able to register with the correct age range. Should be able to register.
     *
     * @throws InterruptedException if the user is not able to register with the correct details
     */
    @Test
    public void testRegister_ValidAgeLowerBound() throws InterruptedException {
        controller.username.setText("validAgeLowerBoundTest");
        controller.name.setText("Testing");
        controller.age.setText("6");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        runAndWait(() -> {
            // Check that the Student was added to the DB by username
            Database<Student> manageStudents = new Database<>(Student.class);
            assertTrue(!manageStudents.queryByField("username", "validAgeLowerBoundTest").isEmpty());

            //delete this student for further use
            manageStudents.delByFiled("username", "validAgeLowerBoundTest");
        });
    }

    /**
     * Checks if the user is able to register with the correct username. Should be able to register.
     *
     * @throws InterruptedException if the user is not able to register with the correct details
     */
    @Test
    public void testRegister_ValidUsername() throws InterruptedException {
        controller.username.setText("uniqueUsernameTest");
        controller.name.setText("Testing");
        controller.age.setText("21");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        runAndWait(() -> {
            // Check that the Student was added to the DB by username
            Database<Student> manageStudents = new Database<>(Student.class);
            assertFalse(manageStudents.queryByField("username", "uniqueUsernameTest").isEmpty());
            //delete this student for further use
            manageStudents.delByFiled("username", "uniqueUsernameTest");
        });

    }

    /**
     * Checks whether the user is able to register with the correct details
     *
     * @throws InterruptedException if the Platform.runLater does not complete in time
     */
    @Test
    public void testRegister_Correct() throws InterruptedException{
        controller.username.setText("testingFromTest");
        controller.name.setText("Testing");
        controller.age.setText("21");
        controller.password.setText("password");
        controller.confirm_password.setText("password");
        controller.department.setText("department");

        runAndWait(() -> controller.register(null));

        //Run below 2 seconds later to ensure the student is added to the DB


        //Check that the Student was added to the DB by username
        runAndWait(() -> {
            Database<Student> manageStudents = new Database<>(Student.class);
            assertFalse(manageStudents.queryByField("username", "testingFromTest").isEmpty());
            manageStudents.delByFiled("username", "testingFromTest");
        });

        //Database<Student> manageStudents = new Database<>(Student.class);
        //assertFalse(manageStudents.queryByField("username", "testingFromTest").isEmpty());
        //delete this student for further use
    }

}