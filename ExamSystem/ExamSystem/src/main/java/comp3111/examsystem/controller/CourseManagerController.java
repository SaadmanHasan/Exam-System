package comp3111.examsystem.controller;

import java.util.List;

import comp3111.examsystem.Classes.Course;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import static comp3111.examsystem.Utils.MsgSender.showMsg;

/**
 * Controller class for managing courses in the exam system.
 */
public class CourseManagerController {

    @FXML
    private TableColumn<Course, String> courseIDCol;

    @FXML
    private TableColumn<Course, String> courseNameCol;

    @FXML
    private TableColumn<Course, String> departmentCol;

    @FXML
    TextField courseID;

    @FXML
    TextField courseName;

    @FXML
    TextField department;

    @FXML
    TextField newCourseID;

    @FXML
    TextField newCourseName;

    @FXML
    TextField newDepartment;

    @FXML
    TableView<Course> table;

    ObservableList<Course> courseObservableList;

    Course selectedCourse;
    Database<Course> courseDB = new Database<>(Course.class);

    /**
     * Validates the input fields for adding or updating a course.
     *
     * @return true if all fields are valid, false otherwise.
     */
    boolean validate() {
        return !newCourseID.getText().isEmpty() &&
                newCourseID.getText().length() <= 8 &&
                !newCourseName.getText().isEmpty() &&
                !newDepartment.getText().isEmpty();
    }

    /**
     * Adds a new course to the database.
     *
     * @param event the action event triggered by the add button.
     */
    @FXML
    void  Add(ActionEvent event) {
        Platform.runLater(() -> {
            if (!validate()) {
                showMsg("Invalid course details");
                return;
            }
            Long uniqueId = System.currentTimeMillis();
            if (!courseDB.queryByField("courseID", newCourseID.getText()).isEmpty()) { // check if courseID already exists
                showMsg("Course ID already exists");
                return;
            }
            courseDB.add(new Course(uniqueId, newCourseID.getText(), newCourseName.getText(), newDepartment.getText(), null));
            Refresh(event);
            clearFields();
        });
    }

    /**
     * Deletes the selected course from the database.
     *
     * @param event the action event triggered by the delete button.
     */
    @FXML
    void Delete(ActionEvent event) {
        Platform.runLater(() -> {
            if (selectedCourse == null) {
                showMsg("Please select a course to delete");
                return;
            }
            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            List<Quiz> quizList = quizDatabase.queryByField("courseID", selectedCourse.getCourseID());
            for (Quiz quiz : quizList) {
                quizDatabase.delByKey(String.valueOf(quiz.getId()));
            }
            courseDB.delByKey(String.valueOf(selectedCourse.getId()));
            Refresh(event);
            clearFields();
        });
    }

    /**
     * Filters the course list based on the input fields.
     *
     * @param event the action event triggered by the filter button.
     */
    @FXML
    void Filter(ActionEvent event) {
        Platform.runLater(() -> {
            FilteredList<Course> filteredData = new FilteredList<>(courseObservableList, p -> true);

            filteredData.setPredicate(course -> {
                if (!courseName.getText().isEmpty() && !course.getCourseName().contains(courseName.getText())) {
                    return false;
                }
                if (!department.getText().isEmpty() && !course.getDepartment().contains(department.getText())) {
                    return false;
                }
                if (!courseID.getText().isEmpty() && !course.getCourseID().contains(courseID.getText())) {
                    return false;
                }

                // Add more conditions as needed
                return true;
            });

            table.setItems(filteredData);
        });
    }

    /**
     * Clears the input fields.
     */
    void clearFields() {
        Platform.runLater(() -> {
            newCourseName.clear();
            newCourseID.clear();
            newDepartment.clear();
        });
    }

    /**
     * Refreshes the course list and updates the table view.
     *
     * @param event the action event triggered by the refresh button.
     */
    @FXML
    void Refresh(ActionEvent event) {
        Platform.runLater(() -> {
            List<Course> courseList = courseDB.getAll();
            courseObservableList = FXCollections.observableArrayList(courseList);
            Filter(event);
        });
    }

    /**
     * Resets the filter fields and updates the table view.
     *
     * @param event the action event triggered by the reset button.
     */
    @FXML
    void Reset(ActionEvent event) {
        Platform.runLater(() -> {
            courseName.clear();
            department.clear();
            courseID.clear();
            table.setItems(courseObservableList);
            clearFields();
        });
    }

    /**
     * Updates the selected course's details in the database.
     *
     * @param event the action event triggered by the update button.
     */
    @FXML
    void Update(ActionEvent event) {
        Platform.runLater(() -> {
            if (!validate()) {
                showMsg("Invalid course details");
                return;
            }
            if (selectedCourse == null) {
                showMsg("Please select a course to update");
                return;
            }
            if (!courseDB.queryByField("courseID", newCourseID.getText()).isEmpty() && !newCourseID.getText().equals(selectedCourse.getCourseID())) { // check if courseID already exists
                showMsg("Course ID already exists");
                return;
            }
            courseDB.update(new Course(selectedCourse.getId(), selectedCourse.getCourseID(), newCourseName.getText(), newDepartment.getText(), null));
            Refresh(event);
        });
    }

    /**
     * Handles the selection of an entry in the course table.
     *
     * @param event the mouse event triggered by selecting an entry.
     */
    @FXML
    void selectEntry(MouseEvent event) {
        Platform.runLater(() -> {
            selectedCourse = table.getSelectionModel().getSelectedItem();
            if (selectedCourse != null) {
                newCourseID.setText(selectedCourse.getCourseID());
                newCourseName.setText(selectedCourse.getCourseName());
                newDepartment.setText(selectedCourse.getDepartment());
            }
        });
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    void initialize() {
        Platform.runLater(() -> {
            courseIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseID()));
            courseNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
            departmentCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));

            Database<Course> courseDB = new Database<>(Course.class);
            List<Course> courseList = courseDB.getAll();
            courseObservableList = FXCollections.observableArrayList(courseList);
            table.setItems(courseObservableList);
        });
    }
}