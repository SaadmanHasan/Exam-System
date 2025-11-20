//Shaun
package comp3111.examsystem.controller;

import java.util.ArrayList;
import java.util.List;

import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Utils.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import static comp3111.examsystem.Utils.MsgSender.showMsg;
/**
 * Controller class for managing students in the exam system.
 */
public class StudentManagerController {

    @FXML
    private TableColumn<Student, String> ageCol;

    @FXML
    private TextField department;

    @FXML
    private TableColumn<Student, String> departmentCol;

    @FXML
    private TableColumn<Student, String> genderCol;

    @FXML
    TextField name;

    @FXML
    private TableColumn<Student, String> nameCol;

    @FXML
    TextField newAge;

    @FXML
    TextField newDepartment;

    @FXML
    MenuButton newGender;

    @FXML
    TextField newName;

    @FXML
    TextField newPassword;

    @FXML
    TextField newUsername;

    @FXML
    private TableColumn<Student, String> passwordCol;

    @FXML
    TableView<Student> table;

    @FXML
    private TextField username;

    @FXML
    private TableColumn<Student, String> usernameCol;

    ObservableList<Student> studentObservableList;

    Student selectedStudent;

    Database<Student> studentDB = new Database<>(Student.class);
    /**
     * Validates the input fields for adding or updating a student.
     *
     * @return true if all fields are valid, false otherwise.
     */
    boolean validate() {
        return !newUsername.getText().isEmpty() &&
                !newPassword.getText().isEmpty() &&
                !newName.getText().isEmpty() &&
                !newAge.getText().isEmpty() && newAge.getText().matches("\\d+") &&
                Integer.parseInt(newAge.getText()) >= 6 && Integer.parseInt(newAge.getText()) <= 100 &&
                !newDepartment.getText().isEmpty() &&
                !newGender.getText().isEmpty();
    }
    /**
     * Adds a new student to the database.
     *
     * @param event the action event triggered by the add button.
     */
    @FXML
    void Add(ActionEvent event) {
        if (!validate()) {
            showMsg("Invalid user details");
            return;
        }
        if (!studentDB.queryByField("username", newUsername.getText()).isEmpty() ) {
            showMsg("Username already exists");
            return;
        }
        Long uniqueId = System.currentTimeMillis();
        studentDB.add(new Student(uniqueId, newUsername.getText(), newPassword.getText(), newName.getText(), newAge.getText(), newDepartment.getText(), newGender.getText()));
        Refresh(event);
        clearFields();
    }
    /**
     * Clears the input fields.
     */
    void clearFields(){
        newName.clear();
        newPassword.clear();
        newUsername.clear();
        newAge.clear();
        newDepartment.clear();
        newGender.setText("Male");
    }
    /**
     * Deletes the selected student from the database.
     *
     * @param event the action event triggered by the delete button.
     */
    @FXML
    void Delete(ActionEvent event) {
        Database<Quiz>  quizDB = new Database<>(Quiz.class);
        List<Quiz> quizList = quizDB.getAll();
        if (selectedStudent == null) {
            showMsg("Please select a student to delete");
            return;
        }
        for (Quiz quiz : quizList) {
            for (int i =0; i<quiz.getStudents().size(); i++) {
                if (quiz.getStudents().get(i).equals(String.valueOf(selectedStudent.getId()))) {
                    quiz.getStudents().remove(i);
                    quiz.getScores().remove(i);
                    quiz.getTimeSpent().remove(i);
                    quizDB.update(quiz);
                    break;
                }
            }
        }

        studentDB.delByKey(String.valueOf(selectedStudent.getId()));
        Refresh(event);
        clearFields();
    }
    /**
     * Filters the student list based on the input fields.
     *
     * @param event the action event triggered by the filter button.
     */
    @FXML
    void Filter(ActionEvent event) {
        FilteredList<Student> filteredData = new FilteredList<>(studentObservableList, p -> true);

        filteredData.setPredicate(student -> {
            if (!name.getText().isEmpty() && !student.getFirstName().contains(name.getText())) {
                return false;
            }
            if (!department.getText().isEmpty() && !student.getDepartment().contains(department.getText())) {
                return false;
            }
            if (!username.getText().isEmpty() && !student.getUsername().contains(username.getText())) {
                return false;
            }

            // Add more conditions as needed
            return true;
        });

        table.setItems(filteredData);
    }
    /**
     * Refreshes the student list and updates the table view.
     *
     * @param event the action event triggered by the refresh button.
     */
    @FXML
    void Refresh(ActionEvent event) {
        List<Student> studentList = studentDB.getAll();
        studentObservableList = FXCollections.observableArrayList(studentList);
        Filter(event);
    }
    /**
     * Resets the filter fields and updates the table view.
     *
     * @param event the action event triggered by the reset button.
     */
    @FXML
    void Reset(ActionEvent event) {
        name.clear();
        department.clear();
        username.clear();
        table.setItems(studentObservableList);
        clearFields();
    }
    /**
     * Updates the selected student's details in the database.
     *
     * @param event the action event triggered by the update button.
     */
    @FXML
    void Update(ActionEvent event) {
        if (!validate()) {
            showMsg("Invalid user details");
            return;
        }
        if(selectedStudent == null){
            showMsg("Please select a student to update");
            return;
        }
        if (!studentDB.queryByField("username", newUsername.getText()).isEmpty() && !newUsername.getText().equals(selectedStudent.getUsername())) {
            showMsg("Username already exists");
            return;
        }
        studentDB.update(new Student(selectedStudent.getId(), newUsername.getText(), newPassword.getText(), newName.getText(), newAge.getText(), newDepartment.getText(), newGender.getText()));
        Refresh(event);
    }
    /**
     * Handles the selection of an entry in the student table.
     *
     * @param event the mouse event triggered by selecting an entry.
     */
    @FXML
    void selectEntry(MouseEvent event) {
        selectedStudent = table.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            newUsername.setText(selectedStudent.getUsername());
            newPassword.setText(selectedStudent.getPassword());
            newName.setText(selectedStudent.getFirstName());
            newAge.setText(selectedStudent.getAge());
            newDepartment.setText(selectedStudent.getDepartment());
            newGender.setText(selectedStudent.getGender());
        }
    }
    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    void initialize() {

        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        ageCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAge()));
        genderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
        departmentCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));
        passwordCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));

        List<Student> studentList = studentDB.getAll();
        studentObservableList = FXCollections.observableArrayList(studentList);
        table.setItems(studentObservableList);
    }
    /**
     * Handles the selection of a new gender from the menu.
     *
     * @param actionEvent the action event triggered by selecting a new gender.
     */
    @FXML
    public void handleNewGender(ActionEvent actionEvent) {
        newGender.setText(((javafx.scene.control.MenuItem) actionEvent.getSource()).getText());
    }
}
