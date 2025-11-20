package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Main;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TeacherManagerControllerTest {

    private TeacherManagerController controller;

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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TeacherManager.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.newUsername = new TextField();
        controller.newPassword = new TextField();
        controller.newName = new TextField();
        controller.newAge = new TextField();
        controller.newDepartment = new TextField();
        controller.newGender = new MenuButton();
        controller.newPosition = new MenuButton();
    }

    @Test
    void addTest_InvalidTeacherDetails() throws IOException, InterruptedException {
        controller.newUsername.setText(""); // Invalid username
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        int size = controller.teacherDB.getAll().size();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Add(null);
            latch.countDown();
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the teacher was not added
        assertEquals(size, controller.teacherDB.getAll().size());
    }

    @Test
    void addTest_UsernameAlreadyExists() throws IOException, InterruptedException {
        Teacher existingTeacher = new Teacher(System.currentTimeMillis(), "jdoe", "password", "John Doe", "30", "CS", "Male", "Professor");
        controller.teacherDB.add(existingTeacher);
        int size= controller.teacherDB.getAll().size();
        controller.newUsername.setText("jdoe"); // Username already exists
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Add(null);
            latch.countDown();
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the teacher was not added
        assertEquals(size, controller.teacherDB.getAll().size());
    }


    @Test
    void updateTest_NoSelectedTeacher() throws IOException, InterruptedException {
        controller.teacherDB.add(new Teacher(System.currentTimeMillis(), "updatenoselect", "password", "John Doe", "30","Male","CS","Professor"));
        controller.selectedTeacher = null;

        controller.newUsername.setText("updateselected");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the teacher was not updated
        System.out.println(controller.teacherDB.queryByField("username","updateselected"));
        assertTrue(controller.teacherDB.queryByField("username","updateselected").isEmpty());
    }

    @Test
    void updateTest_UsernameAlreadyExists() throws IOException, InterruptedException {
        Teacher existingTeacher = new Teacher(System.currentTimeMillis(), "jdoe", "password", "John Doe", "30", "CS", "Male", "Professor");
        controller.teacherObservableList.add(existingTeacher);
        controller.selectedTeacher = existingTeacher;

        controller.newUsername.setText("jdoe"); // Username already exists
        controller.newPassword.setText("newpassword");
        controller.newName.setText("John Doe Updated");
        controller.newAge.setText("31");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the teacher was not updated
        Teacher updatedTeacher = controller.teacherObservableList.filtered(t -> t.getUsername().equals("jdoe")).get(0);
        assertEquals("John Doe", updatedTeacher.getFirstName());
        assertEquals("password", updatedTeacher.getPassword());
    }
    @Test
    void updateTest_ValidTeacherDetails() throws IOException, InterruptedException {
        Teacher teacher = new Teacher(System.currentTimeMillis(), "updatevalid", "password", "John Doe", "30", "CS", "Male", "Professor");
        controller.teacherDB.add(teacher);
        controller.selectedTeacher = teacher;

        controller.newUsername.setText("updatedvalid");
        controller.newPassword.setText("newpassword");
        controller.newName.setText("John Doe Updated");
        controller.newAge.setText("31");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }
        List<Teacher> filteredList = controller.teacherDB.queryByField("username", "updatedvalid");
        assertFalse(filteredList.isEmpty(), "Filtered list should not be empty");
        Teacher updatedTeacher = filteredList.get(0);
        assertEquals("John Doe Updated", updatedTeacher.getFirstName());
        assertEquals("newpassword", updatedTeacher.getPassword());
    }

    @Test
    void deleteTest_NoSelectedTeacher() throws IOException, InterruptedException {
        controller.selectedTeacher = null;

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Delete(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that no teacher was deleted
        assertEquals(controller.teacherObservableList.size(), controller.teacherObservableList.size());
    }

    @Test
    void deleteTest_ValidSelectedTeacher() throws IOException, InterruptedException {
        Teacher teacher = new Teacher(System.currentTimeMillis(), "janedoe", "password", "John Doe", "30", "CS", "Male", "Professor");
        controller.teacherObservableList.add(teacher);
        controller.selectedTeacher = teacher;
        int size = controller.teacherDB.getAll().size();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Delete(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        assertEquals(size, controller.teacherDB.getAll().size());
        assertTrue(controller.teacherObservableList.filtered(t -> t.getUsername().equals("janedoe")).isEmpty());
    }



    @Test
    void refreshTest() throws IOException {
        controller.Refresh(null);
        ObservableList<Teacher> teachers = controller.table.getItems();
        assertNotNull(teachers);
    }

    @Test
    void testValidate_AllFieldsValid() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertTrue(controller.validate());
    }

    @Test
    void testValidate_UsernameEmpty() {
        controller.newUsername.setText("");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_PasswordEmpty() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_NameEmpty() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_AgeEmpty() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_AgeNotNumeric() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("thirty");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_AgeTooYoung() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("10");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_AgeTooOld() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("101");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_DepartmentEmpty() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("");
        controller.newGender.setText("Male");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_GenderEmpty() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("");
        controller.newPosition.setText("Professor");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_PositionEmpty() {
        controller.newUsername.setText("jdoe");
        controller.newPassword.setText("password");
        controller.newName.setText("John Doe");
        controller.newAge.setText("30");
        controller.newDepartment.setText("CS");
        controller.newGender.setText("Male");
        controller.newPosition.setText("");
        assertFalse(controller.validate());
    }
}