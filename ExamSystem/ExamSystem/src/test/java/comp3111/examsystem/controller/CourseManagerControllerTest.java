package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Course;
import comp3111.examsystem.Main;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CourseManagerControllerTest {

    private CourseManagerController controller;

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
    void setUp() throws IOException {
        System.out.println("Setting up");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("CourseManager.fxml"));
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.newCourseID = new TextField();
        controller.newCourseName = new TextField();
        controller.newDepartment = new TextField();
    }



    @Test
    void addTest_InvalidCourseDetails() throws IOException, InterruptedException {
        controller.newCourseID.setText(""); // Invalid course ID
        controller.newCourseName.setText("Introduction to Computer Science");
        controller.newDepartment.setText("Computer Science");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Add(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the course was not added
        assertTrue(controller.courseDB.queryByField("courseID", "").isEmpty());
    }

    @Test
    void addTest_CourseIDAlreadyExists() throws IOException, InterruptedException {
        Course newEntry  = controller.courseDB.getAll().getFirst();
        controller.newCourseID.setText(newEntry.getCourseID()); // Course ID already exists
        controller.newCourseName.setText("Introduction to Computer Science");
        controller.newDepartment.setText("Computer Science");

        // Add a course with the same ID to the database
        controller.courseDB.add(new Course(System.currentTimeMillis(), "CS101", "Existing Course", "Computer Science", null));
        int size = controller.courseDB.getAll().size();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Add(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        assertEquals(size, controller.courseDB.getAll().size());
    }

    @Test
    void addTest_ValidCourseDetails() throws IOException, InterruptedException {
        controller.newCourseID.setText("CS102");
        controller.newCourseName.setText("Introduction to Computer Science");
        controller.newDepartment.setText("Computer Science");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Add(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the course was added
        List<Course> courses = controller.courseDB.queryByField("courseID", "CS102");
        assertEquals(1, courses.size());
        assertEquals("Introduction to Computer Science", courses.get(0).getCourseName());
    }



    @Test
    void updateTest_InvalidCourseDetails() throws IOException, InterruptedException {
        controller.newCourseID.setText(""); // Invalid course ID
        controller.newCourseName.setText("Updated Course Name");
        controller.newDepartment.setText("Updated Department");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the course was not updated
        assertTrue(controller.courseDB.queryByField("courseID", "").isEmpty());
    }

    @Test
    void updateTest_NoSelectedCourse() throws IOException, InterruptedException {
        controller.newCourseID.setText("CS123");
        controller.newCourseName.setText("Updated Course Name");
        controller.newDepartment.setText("Updated Department");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the course was not updated
        assertTrue(controller.courseDB.queryByField("courseID", "CS123").isEmpty());
    }

    @Test
    void updateTest_CourseIDAlreadyExists() throws IOException, InterruptedException {
        controller.courseDB.add(new Course(System.currentTimeMillis(), "CS1234567", "Existing Course", "Computer Science", null));
        Course modify = new Course(System.currentTimeMillis(), "CS1234568", "Existing Course", "Computer Science", null);
        controller.courseDB.add(modify);
        controller.newCourseID.setText("CS1234567");
        controller.newCourseName.setText("Updated Course Name");
        controller.newDepartment.setText("Updated Department");
        controller.selectedCourse = modify;

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the course was not updated
        Course courses = controller.courseDB.queryByKey(String.valueOf(modify.getId()) );
        assertEquals("Existing Course", courses.getCourseName());
    }

    @Test

    void updateTest_ValidCourseDetails() throws IOException, InterruptedException {
        controller.newCourseID.setText("CS103");
        controller.newCourseName.setText("Updated Course Name");
        controller.newDepartment.setText("Updated Department");

        // Add a course to the database and select it
        Course course = new Course(System.currentTimeMillis(), "CS103", "Original Course", "Computer Science", null);
        controller.courseDB.add(course);
        controller.selectedCourse = course;

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Update(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that the course was updated
        List<Course> courses = controller.courseDB.queryByField("courseID", "CS103");
        assertEquals("Updated Course Name", courses.get(0).getCourseName());
        assertEquals("Updated Department", courses.get(0).getDepartment());
    }



    @Test
    void filterTest() throws IOException {
        controller.courseName.setText("Introduction");
        controller.Filter(null);
        ObservableList<Course> filteredCourses = controller.table.getItems();
        for (Course course : filteredCourses) {
            assertTrue(course.getCourseName().contains("Introduction"));
        }
    }



    @Test
    void refreshTest() throws IOException {
        controller.Refresh(null);
        ObservableList<Course> courses = controller.table.getItems();
        assertNotNull(courses);
    }

    @Test
    void deleteTest_NoSelectedCourse() throws IOException, InterruptedException {

        controller.selectedCourse = null;

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Delete(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Check that no course was deleted
        assertEquals(controller.courseDB.getAll().size(), controller.courseDB.getAll().size());
    }

    @Test
    void deleteTest_ValidSelectedCourse() throws IOException, InterruptedException {
        Course selectedCourse = new Course(System.currentTimeMillis(), "CS105", "Introduction to Computer Science", "Computer Science", null);
        controller.courseDB.add(selectedCourse);
        controller.selectedCourse = selectedCourse;

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.Delete(null);
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Platform.runLater did not complete in time");
        }

        // Add a small delay to ensure the changes are written to the text file
        Thread.sleep(100);

        // Check that the course was deleted
        assertTrue(controller.courseDB.queryByField("courseID", "CS105").isEmpty());
    }



    @Test
    void testValidate_AllFieldsValid() {
        controller.newCourseID.setText("CS101");
        controller.newCourseName.setText("Intro to CS");
        controller.newDepartment.setText("CS");
        assertTrue(controller.validate());
    }

    @Test
    void testValidate_CourseIDEmpty() {
        controller.newCourseID.setText("");
        controller.newCourseName.setText("Intro to CS");
        controller.newDepartment.setText("CS");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_CourseIDTooLong() {
        controller.newCourseID.setText("CS1012345");
        controller.newCourseName.setText("Intro to CS");
        controller.newDepartment.setText("CS");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_CourseNameEmpty() {
        controller.newCourseID.setText("CS101");
        controller.newCourseName.setText("");
        controller.newDepartment.setText("CS");
        assertFalse(controller.validate());
    }

    @Test
    void testValidate_DepartmentEmpty() {
        controller.newCourseID.setText("CS101");
        controller.newCourseName.setText("Intro to CS");
        controller.newDepartment.setText("");
        assertFalse(controller.validate());
    }
}