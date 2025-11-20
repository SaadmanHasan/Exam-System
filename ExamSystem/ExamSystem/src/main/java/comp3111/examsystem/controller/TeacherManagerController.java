package comp3111.examsystem.controller;

import java.util.List;

import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Utils.Database;
import javafx.application.Platform;
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
 * Controller class for managing teachers in the exam system.
 */
public class TeacherManagerController {

    @FXML
    private TableColumn<Teacher, String> positionCol;

    @FXML
    private TableColumn<Teacher, String> ageCol;

    @FXML
    private TextField department;

    @FXML
    private TableColumn<Teacher, String> departmentCol;

    @FXML
    private TableColumn<Teacher, String> genderCol;

    @FXML
    TextField name;

    @FXML
    private TableColumn<Teacher, String> nameCol;

    @FXML
    TextField newAge;

    @FXML
    TextField newDepartment;

    @FXML
    MenuButton newGender;

    @FXML
    MenuButton newPosition;

    @FXML
    TextField newName;

    @FXML
    TextField newPassword;

    @FXML
    TextField newUsername;

    @FXML
    private TableColumn<Teacher, String> passwordCol;

    @FXML
    TableView<Teacher> table;

    @FXML
    private TextField username;

    @FXML
    private TableColumn<Teacher, String> usernameCol;

    ObservableList<Teacher> teacherObservableList= FXCollections.observableArrayList();

    Teacher selectedTeacher;
    Database<Teacher> teacherDB = new Database<>(Teacher.class);

    /**
     * Validates the input fields for adding or updating a teacher.
     *
     * @return true if all fields are valid, false otherwise.
     */
    boolean validate() {
        return !newUsername.getText().isEmpty() &&
                !newPassword.getText().isEmpty() &&
                !newName.getText().isEmpty() &&
                !newAge.getText().isEmpty() && newAge.getText().matches("\\d+") &&
                Integer.parseInt(newAge.getText()) >= 15 && Integer.parseInt(newAge.getText()) <= 100 &&
                !newDepartment.getText().isEmpty() &&
                !newGender.getText().isEmpty() &&
                !newPosition.getText().isEmpty();
    }

    /**
     * Adds a new teacher to the database.
     *
     * @param event the action event triggered by the add button.
     */
    @FXML
    void Add(ActionEvent event) {
        Platform.runLater(() -> {
            if (!validate()) {
                showMsg("Invalid user details");
                return;
            }
            if (!teacherDB.queryByField("username", newUsername.getText()).isEmpty()) {
                showMsg("Username already exists");
                return;
            }
            Long uniqueId = System.currentTimeMillis();
            teacherDB.add(new Teacher(uniqueId, newUsername.getText(), newPassword.getText(), newName.getText(), newAge.getText(), newDepartment.getText(), newGender.getText(), newPosition.getText()));
            Refresh(event);
            clearFields();
        });
    }

    /**
     * Deletes the selected teacher from the database.
     *
     * @param event the action event triggered by the delete button.
     */
    @FXML
    void Delete(ActionEvent event) {
        Platform.runLater(() -> {
            if (selectedTeacher == null) {
                showMsg("Please select a teacher to delete");
                return;
            }
            Database<Quiz> quizDatabase = new Database<>(Quiz.class);
            List<Quiz> quizList = quizDatabase.queryByField("teachername", selectedTeacher.getUsername());
            for (Quiz quiz : quizList) {
                quizDatabase.delByKey(String.valueOf(quiz.getId()));
            }
            teacherDB.delByKey(String.valueOf(selectedTeacher.getId()));
            Refresh(event);
            clearFields();
        });
    }

    /**
     * Filters the teacher list based on the input fields.
     *
     * @param event the action event triggered by the filter button.
     */
    @FXML
    void Filter(ActionEvent event) {
        Platform.runLater(() -> {
            FilteredList<Teacher> filteredData = new FilteredList<>(teacherObservableList, p -> true);

            filteredData.setPredicate(teacher -> {
                if (!name.getText().isEmpty() && !teacher.getFirstName().contains(name.getText())) {
                    return false;
                }
                if (!department.getText().isEmpty() && !teacher.getDepartment().contains(department.getText())) {
                    return false;
                }
                if (!username.getText().isEmpty() && !teacher.getUsername().contains(username.getText())) {
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
            newName.clear();
            newPassword.clear();
            newUsername.clear();
            newAge.clear();
            newDepartment.clear();
            newGender.setText("Male");
            newPosition.setText("Position");
        });
    }

    /**
     * Refreshes the teacher list and updates the table view.
     *
     * @param event the action event triggered by the refresh button.
     */
    @FXML
    void Refresh(ActionEvent event) {
        Platform.runLater(() -> {
            List<Teacher> teacherList = teacherDB.getAll();
            teacherObservableList = FXCollections.observableArrayList(teacherList);
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
            name.clear();
            department.clear();
            username.clear();
            table.setItems(teacherObservableList);
            clearFields();
        });
    }

    /**
     * Updates the selected teacher's details in the database.
     *
     * @param event the action event triggered by the update button.
     */
    @FXML
    void Update(ActionEvent event) {
        Platform.runLater(() -> {
            if (!validate()) {
                showMsg("Invalid user details");
                return;
            }
            if (selectedTeacher == null) {
                showMsg("Please select a teacher to update");
                return;
            }
            if (!newUsername.getText().equals(selectedTeacher.getUsername()) && !teacherDB.queryByField("username", newUsername.getText()).isEmpty()) {
                showMsg("Username already exists");
                return;
            }
            System.out.println("Updating teacher");
            teacherDB.update(new Teacher(selectedTeacher.getId(), newUsername.getText(), newPassword.getText(), newName.getText(), newAge.getText(), newDepartment.getText(), newGender.getText(), newPosition.getText()));
            Refresh(event);
        });
    }

    /**
     * Handles the selection of an entry in the teacher table.
     *
     * @param event the mouse event triggered by selecting an entry.
     */
    @FXML
    void selectEntry(MouseEvent event) {
        Platform.runLater(() -> {
            selectedTeacher = table.getSelectionModel().getSelectedItem();
            if (selectedTeacher != null) {
                newUsername.setText(selectedTeacher.getUsername());
                newPassword.setText(selectedTeacher.getPassword());
                newName.setText(selectedTeacher.getFirstName());
                newAge.setText(selectedTeacher.getAge());
                newDepartment.setText(selectedTeacher.getDepartment());
                newGender.setText(selectedTeacher.getGender());
                newPosition.setText(selectedTeacher.getPosition());
            }
        });
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    void initialize() {
        Platform.runLater(() -> {
            usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
            nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
            ageCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAge()));
            genderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
            departmentCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));
            passwordCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
            positionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPosition()));

            List<Teacher> teacherList = teacherDB.getAll();
            teacherObservableList = FXCollections.observableArrayList(teacherList);
            table.setItems(teacherObservableList);
        });
    }

    /**
     * Handles the selection of a new position from the menu.
     *
     * @param actionEvent the action event triggered by selecting a new position.
     */
    @FXML
    public void handleNewPosition(ActionEvent actionEvent) {
        newPosition.setText(((javafx.scene.control.MenuItem) actionEvent.getSource()).getText());
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